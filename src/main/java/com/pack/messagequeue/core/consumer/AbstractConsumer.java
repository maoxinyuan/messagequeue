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

/**
 * @author maoxy
 * @date 2019/1/18 15:07
 */
public abstract class AbstractConsumer {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractConsumer.class);

    @Autowired
    private RabbitConfig rabbitConfig;

    public void createConsumer(String queueName) throws IOException, TimeoutException {
        Connection connection = rabbitConfig.getConnection();
        Channel channel = connection.createChannel();
        onMessage(channel,queueName);
    }

    private void onMessage(Channel channel,String queueName) throws IOException {
        BasicQueue basicQueue = constructBasicQueue(queueName);
        LOGGER.info("register WorkQueueConsumer...queueName:{}", basicQueue.getQueueName());
        channel.queueDeclare(basicQueue.getQueueName(),basicQueue.isDurable(),basicQueue.isExclusive(),basicQueue.isAutoDelete(),basicQueue.getArguments());
        channel.basicQos(basicQueue.getPrefetchCount());
        DefaultConsumer consumer = newConsumer(channel);
        /**
         * 采用公平分发，关闭自动应答
         */
        channel.basicConsume(basicQueue.getQueueName(),basicQueue.isAutoAck(),consumer);
    }

    /**
     *  构造BasicQueue
     * @return BasicQueue
     */
    public abstract BasicQueue constructBasicQueue(String queueName);

    public abstract DefaultConsumer newConsumer(Channel channel);

}
