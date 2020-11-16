package com.pacific.messagequeue.core.consumer;

import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.core.model.BasicQueue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author maoxy
 * @date 2019/1/11 16:04
 * 延时队列
 */
@Component
public class DelayedExchangeConsumer extends AbstractExchangeConsumer{

    @Value("${rabbit.delayed.dlx.exchange}")
    private String exchangeName;

    @Override
    public String exchangeName() {
        return exchangeName;
    }

    @Override
    public BasicQueue constructBasicQueue(String queueName) {
        return new BasicQueue(queueName,true);
    }

    @Override
    public DefaultConsumer newConsumer(Channel channel) {
        return new MessageConsumer(channel);
    }

    @Override
    public int basicQos() {
        return MessageContantValue.PREFETCH_COUNT;
    }

}
