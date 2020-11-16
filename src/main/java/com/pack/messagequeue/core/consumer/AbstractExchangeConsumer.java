package com.pacific.messagequeue.core.consumer;

import com.pacific.messagequeue.config.RabbitConfig;
import com.pacific.messagequeue.core.model.BasicQueue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractExchangeConsumer {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractExchangeConsumer.class);

    @Autowired
    private RabbitConfig rabbitConfig;

    public void createConsumer(String queueName,String routingKey) throws IOException, TimeoutException {
        Connection connection = rabbitConfig.getConnection();
        Channel channel = connection.createChannel();
        onMessage(channel,queueName,routingKey);
    }

    private void onMessage(Channel channel,String queueName,String routingKey) throws IOException {
        BasicQueue basicQueue = constructBasicQueue(queueName);
        LOGGER.info("register WorkQueueConsumer...queueName:{}", basicQueue.getQueueName());
        channel.queueDeclare(queueName,basicQueue.isDurable(),basicQueue.isExclusive(),basicQueue.isAutoDelete(),basicQueue.getArguments());
        channel.queueBind(queueName, exchangeName(), routingKey);
        channel.basicQos(basicQos());
        DefaultConsumer consumer = newConsumer(channel);
        /**
         * 采用公平分发，关闭自动应答
         */
        channel.basicConsume(basicQueue.getQueueName(),basicQueue.isAutoAck(),consumer);
    }

    /**
     * 交换机
     */
    public abstract String exchangeName();
    /**
     *  构造BasicQueue
     * @return BasicQueue
     */
    public abstract BasicQueue constructBasicQueue(String queueName);
    /**
     * 消费者
     * @param channel
     * @return
     */
    public abstract DefaultConsumer newConsumer(Channel channel);

    public abstract int basicQos();
}
