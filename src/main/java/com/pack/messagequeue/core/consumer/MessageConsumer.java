package com.pacific.messagequeue.core.consumer;

import com.pacific.messagequeue.service.MessageHandleService;
import com.pacific.messagequeue.config.SpringContext;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author maoxy
 * @date 2019/1/18 15:15
 */
public class MessageConsumer extends DefaultConsumer {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    private Channel channel;

    private MessageHandleService messageHandleService;
    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public MessageConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
        messageHandleService =  SpringContext.getBean(MessageHandleService.class);
    }

    @Override

    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String persistenceMesssage = new String(body, "utf-8");
        try {
            LOGGER.info("recive message... handle message:{}",persistenceMesssage);
            messageHandleService.handleMessage(persistenceMesssage);
        } finally {
            LOGGER.info("consumer ack... message body:{}",persistenceMesssage);
            //ack 手动应答
            channel.basicAck(envelope.getDeliveryTag(),false);
        }
    }

}
