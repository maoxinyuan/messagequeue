package com.pacific.messagequeue.initbean;

import com.pacific.messagequeue.contant.DelayedTimeMap;
import com.pacific.messagequeue.contant.ExchangeType;
import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.contant.ServiceTypeMap;
import com.pacific.messagequeue.core.config.RabbitHelper;
import com.pacific.messagequeue.core.consumer.DelayedExchangeConsumer;
import com.pacific.messagequeue.core.consumer.WorkNormalConsumer;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class DelayedQueueConsumer implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(DelayedQueueConsumer.class);
    @Autowired
    private DelayedExchangeConsumer delayedExchangeConsumer;
    @Autowired
    private RabbitHelper rabbitHelper;
    @Value("delayed.dlx.exchange")
    private String dlxExchangeName;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("------------ start register delayed consumer...");
        Map<Integer, String> timeMap = DelayedTimeMap.getTimeMap();
        timeMap.forEach((k,v)->{
            createConsumer(v);
        });
    }

    public void updateConsumer(Collection<String> oldValues, Collection<String> newValues){
        newValues.stream().forEach(time->{
            if (!oldValues.contains(time)){
                    createConsumer(time);
            }
        });
    }

    private void createConsumer(String value){
        String delayedQueueName = MessageContantValue.PRE_DELAYED_DLX_QUEUENAMW+value;

        try {
            Channel channel = rabbitHelper.getRabbitChannel().getChannel();
            // 创建死信交换器和队列
            channel.exchangeDeclare(dlxExchangeName, ExchangeType.DIRECT, true, false, null);
            channel.queueDeclare(delayedQueueName, true, false, false, null);
            channel.queueBind(delayedQueueName, dlxExchangeName, delayedQueueName);

            delayedExchangeConsumer.createConsumer(delayedQueueName,delayedQueueName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
