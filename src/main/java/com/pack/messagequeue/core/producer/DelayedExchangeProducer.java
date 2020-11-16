package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.ExchangeType;
import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.core.config.RabbitHelper;
import com.pacific.messagequeue.core.confirm.RabbitConfirmListener;
import com.pacific.messagequeue.core.model.RabbitChannel;
import com.pacific.messagequeue.model.PersistenceMesssage;
import com.pacific.messagequeue.utils.JsonUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DelayedExchangeProducer {

    private final static Logger LOGGER = LoggerFactory.getLogger(DelayedExchangeProducer.class);
    @Value("${rabbit.delayed.exchange}")
    private String exchangeName;
    @Value("${rabbit.delayed.dlx.exchange}")
    private String dlxExchangeName;

    @Autowired
    private RabbitHelper rabbitHelper;

    public void sendDelayedMessage(PersistenceMesssage persistenceMesssage,String routingKey, String queueName,String ttl) throws IOException, InterruptedException {
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
            Map<String, Object> arguments = new HashMap<>(16);
            // 为队列设置队列交换器
            arguments.put(MessageContantValue.ARGS_X_DEAD_LETTER_EXCHANGE, dlxExchangeName);
            arguments.put(MessageContantValue.ARGS_X_MESSAGE_TTL, Integer.valueOf(ttl));
            channel.exchangeDeclare(exchangeName, ExchangeType.DIRECT, true, false, null);
            channel.queueDeclare(queueName, true, false, false, arguments);
            channel.queueBind(queueName, exchangeName, routingKey);

            //发送给同一个消费者的消息条数
            channel.basicQos(MessageContantValue.PREFETCH_COUNT);
            //开启confirm模式
            channel.confirmSelect();
            //通道添加监听
            channel.addConfirmListener(new RabbitConfirmListener(persistenceMesssage,channel));
            channel.basicPublish(exchangeName, routingKey, null, json.getBytes());
        }finally {
            rabbitHelper.close(rabbitChannel);
        }


    }

}
