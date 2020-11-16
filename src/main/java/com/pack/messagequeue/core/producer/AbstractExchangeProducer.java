package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.ExchangeType;
import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.core.config.RabbitHelper;
import com.pacific.messagequeue.core.confirm.RabbitConfirmListener;
import com.pacific.messagequeue.core.model.RabbitChannel;
import com.pacific.messagequeue.model.PersistenceMesssage;
import com.pacific.messagequeue.utils.JsonUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;

public abstract class AbstractExchangeProducer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractExchangeProducer.class);
    @Autowired
    private RabbitHelper rabbitHelper;

    public void exchange(PersistenceMesssage persistenceMesssage,String routingKey) throws IOException, InterruptedException {
        if (StringUtils.isEmpty(persistenceMesssage.getMessage().getBody())){
            LOGGER.info("message body connot be null!");
            return;
        }
        String json = JsonUtil.toJson(persistenceMesssage);
        RabbitChannel rabbitChannel = null;
        try {
            rabbitChannel = rabbitHelper.getRabbitChannel();
            Channel channel = rabbitChannel.getChannel();
            //清楚信道监听
            channel.clearConfirmListeners();
            channel.exchangeDeclare(exchangeName(),exchangeType(),isDurable());
            //发送给同一个消费者的消息条数
            channel.basicQos(basicQos());
            //开启confirm模式
            channel.confirmSelect();
            //通道添加监听
            channel.addConfirmListener(new RabbitConfirmListener(persistenceMesssage,channel));
            channel.basicPublish(exchangeName(), routingKey, null, json.getBytes());
        }finally {
            rabbitHelper.close(rabbitChannel);
        }
    }

    /**
     * 交换机
     */
    public abstract String exchangeName();
    /**
     * 交换机类型
     */
    public abstract String exchangeType();
    /**
     * 发送给同一个消费者的消息条数
     */
    public abstract int basicQos();

    public abstract boolean isDurable();

}
