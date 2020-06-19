# rocketmq-start

# RocketMQ对接指南

## 引入依赖包

```xml
		<dependency>
			<groupId>com.xiaoseller</groupId>
			<artifactId>rocketmq-start</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
```

## 加入配置

只作为producer则只需要配置`rocketmq.producerGroupName`  
只作为consumer则只需要配置`rocketmq.consumerGroupName`  
`rocketmq.subscribes`为

```properties
rocketmq.producerGroupName=flash
rocketmq.consumerGroupName=flash
rocketmq.subscribes=test,*;test2,xxx;test3,aaa
```

<table>
    <tr>
        <th>属性名</td>
        <th>含义</td>
        <th>是否必填</td>
    </tr>
    <tr>
        <td>rocketmq.producerGroupName</td>
        <td>生产者groupName，如果是生产者则配，否则不配，一般建议使用应用名</td>
        <td>否</td>
    </tr>
    <tr>
        <td>rocketmq.consumerGroupName</td>
        <td>消费者groupName，如果是消费者则配，否则不配，一般建议使用应用名</td>
        <td>否</td>
    </tr>
    <tr>
        <td>rocketmq.subscribes</td>
        <td>订阅信息，消费者必须填写，格式为 topic,expression;topic,expression.......</td>
        <td>否</td>
    </tr>
</table>

## 加入RocketMQ配置类

`TransactionCheckListener` ：如需要发送事务消息，则该Listener为反查接口，如项目作为生产者则必须存在该Bean
`MessageListener`：如项目作为消费者则必须存在该Bean，订阅消费的入口


```java
/**
 * 
 */
package com.dianwoba.alliance.flash.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * @author Zhu
 * @date 2017年2月25日 下午10:47:57
 * @description
 */
@Configuration
public class RocketMqConfig {

	@Bean
	public TransactionCheckListener transactionCheckListener() {
		return new TransactionCheckListener() {

			@Override
			public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		};
	}

	@Bean
	public MessageListener messageListener() {
		return new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		};
	}
}

```

## 使用

配置工作已经做完了，使用的话直接用`Spring`的依赖注入，注入对应的Bean就可以使用了  
生产者注入`TransactionMQProducer`  
消费者只需要在`MessageListener`的实现类里写相应的消费逻辑即可
