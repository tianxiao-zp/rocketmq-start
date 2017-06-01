package com.xiaoseller.rocketmq.starter;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({ TransactionMQProducer.class, DefaultMQPushConsumer.class })
@EnableConfigurationProperties(RocketmqProperties.class)
public class RocketMqAutoConfiguration {

	private final static Logger LOGGER = LoggerFactory.getLogger(RocketMqAutoConfiguration.class);
	@Autowired
	private RocketmqProperties properties;

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(value = TransactionCheckListener.class)
	public TransactionMQProducer transactionMQProducer(TransactionCheckListener transactionListener) {
		/**
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */
		final TransactionMQProducer producer = new TransactionMQProducer(properties.getProducerGroupName());
		producer.setNamesrvAddr(properties.getNameServerAddr());
		producer.setTransactionCheckListener(transactionListener);
		producer.setDefaultTopicQueueNums(8);
		/**
		 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己
		 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("producer shutdown");
				producer.shutdown();
				LOGGER.info("producer has shutdown");
			}
		}));
		/**
		 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		 * 注意：切记不可以在每次发送消息时，都调用start方法
		 */
		try {
			producer.start();
			LOGGER.info("rocketmq producer started...nameserver:{}, group:{}", properties.getNameServerAddr(),
					properties.getProducerGroupName());
		} catch (MQClientException e) {
			LOGGER.error("producer start error, nameserver:{}, group:{}", properties.getNameServerAddr(),
					properties.getProducerGroupName(), e);
		}
		return producer;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(value = MessageListener.class)
	public DefaultMQPushConsumer defaultMQPushConsumer(MessageListener messageListener) {
		/**
		 * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ConsumerGroupName需要由应用来保证唯一
		 */
		final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerGroupName());
		if (properties.getConsumeThreadMin() != null) {
			consumer.setConsumeThreadMin(properties.getConsumeThreadMin());
		}
		if (properties.getConsumeThreadMax() != null) {
			consumer.setConsumeThreadMax(properties.getConsumeThreadMax());
		}
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		consumer.setNamesrvAddr(properties.getNameServerAddr());
		try {
			String subscribes = properties.getSubscribes();
			if (StringUtils.hasText(subscribes)) {
				String[] topicAndExpressions = subscribes.split(";");
				for (String tes : topicAndExpressions) {
					String[] te = tes.split(",");
					consumer.subscribe(te[0], te[1]);
					LOGGER.info("subsribe, top:{}, expression:{}", te[0], te[1]);
				}
			}
		} catch (MQClientException e) {
			LOGGER.error("consumer subscribe error", e);
		}
		consumer.registerMessageListener(messageListener);
		/**
		 * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("consumer shutdown");
				consumer.shutdown();
				LOGGER.info("consumer has shutdown");
			}
		}));
		try {
			consumer.start();
			LOGGER.info("rocketmq consumer started...nameserver:{}, group:{}", properties.getNameServerAddr(),
					properties.getConsumerGroupName());
		} catch (MQClientException e) {
			LOGGER.error("consumer start error, nameserver:{}, group:{}", properties.getNameServerAddr(),
					properties.getConsumerGroupName(), e);
		}
		return consumer;
	}
}
