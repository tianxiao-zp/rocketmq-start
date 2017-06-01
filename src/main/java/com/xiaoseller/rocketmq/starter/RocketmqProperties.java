package com.xiaoseller.rocketmq.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = RocketmqProperties.ROCKETMQ_PREFIX)
public class RocketmqProperties {

	public static final String ROCKETMQ_PREFIX = "rocketmq";
	private String nameServerAddr;
	private String producerGroupName;
	private String consumerGroupName;
	private Integer consumeThreadMin;
	private Integer consumeThreadMax;
	private String subscribes;

	/**
	 * @return the nameServerAddr
	 */
	public String getNameServerAddr() {
		return nameServerAddr;
	}

	/**
	 * @param nameServerAddr
	 *            the nameServerAddr to set
	 */
	public void setNameServerAddr(String nameServerAddr) {
		this.nameServerAddr = nameServerAddr;
	}

	/**
	 * @return the producerGroupName
	 */
	public String getProducerGroupName() {
		return producerGroupName;
	}

	/**
	 * @param producerGroupName
	 *            the producerGroupName to set
	 */
	public void setProducerGroupName(String producerGroupName) {
		this.producerGroupName = producerGroupName;
	}

	/**
	 * @return the consumerGroupName
	 */
	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	/**
	 * @param consumerGroupName
	 *            the consumerGroupName to set
	 */
	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	/**
	 * @return the consumeThreadMin
	 */
	public Integer getConsumeThreadMin() {
		return consumeThreadMin;
	}

	/**
	 * @param consumeThreadMin
	 *            the consumeThreadMin to set
	 */
	public void setConsumeThreadMin(Integer consumeThreadMin) {
		this.consumeThreadMin = consumeThreadMin;
	}

	/**
	 * @return the consumeThreadMax
	 */
	public Integer getConsumeThreadMax() {
		return consumeThreadMax;
	}

	/**
	 * @param consumeThreadMax
	 *            the consumeThreadMax to set
	 */
	public void setConsumeThreadMax(Integer consumeThreadMax) {
		this.consumeThreadMax = consumeThreadMax;
	}

	/**
	 * @return the subscribes
	 */
	public String getSubscribes() {
		return subscribes;
	}

	/**
	 * @param subscribes
	 *            the subscribes to set
	 */
	public void setSubscribes(String subscribes) {
		this.subscribes = subscribes;
	}
}
