package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.core.confirm.RabbitConfirmListener;
import com.pacific.messagequeue.core.config.RabbitHelper;
import com.pacific.messagequeue.core.model.BasicQueue;
import com.pacific.messagequeue.core.model.RabbitChannel;
import com.pacific.messagequeue.model.Message;
import com.pacific.messagequeue.model.PersistenceMesssage;
import com.pacific.messagequeue.utils.JsonUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author maoxy
 * @date 2019/1/18 12:58
 */
@Component
public class RabbitQueueProducer {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitQueueProducer.class);

    @Autowired
    private RabbitHelper rabbitHelper;

    /**
     * 发送事物消息
     * @param persistenceMesssage
     * @param basicQueue
     * @return
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public void sendTransacitionWorkqueueMessage(PersistenceMesssage persistenceMesssage, BasicQueue basicQueue) {
        if (StringUtils.isEmpty(persistenceMesssage.getMessage().getBody())){
            LOGGER.info("message body connot be null!");
            return;
        }
        String json = JsonUtil.toJson(persistenceMesssage);
        LOGGER.info("============send message start:"+json);
        RabbitChannel rabbitChannel = null;
        try {
            rabbitChannel = rabbitHelper.getRabbitChannel();
            Channel channel = rabbitChannel.getChannel();
            channel.clearConfirmListeners();
            channel.queueDeclare(basicQueue.getQueueName(),basicQueue.isDurable(),basicQueue.isExclusive(),basicQueue.isAutoDelete(),basicQueue.getArguments());
            //发送给同一个消费者的消息条数
            channel.basicQos(MessageContantValue.PREFETCH_COUNT);
            //开启confirm模式
            channel.confirmSelect();
            //通道添加监听
            channel.addConfirmListener(new RabbitConfirmListener(persistenceMesssage,channel));
            channel.basicPublish(basicQueue.getExchange(), basicQueue.getRoutingKey(),basicQueue.getProps(),json.getBytes());
            LOGGER.info("============send message end:"+json);
        } catch (IOException e) {
            LOGGER.info("message send IOException!");
            LOGGER.info(e.getMessage());
        }  catch (InterruptedException e) {
            LOGGER.info("message send InterruptedException!");
            LOGGER.info(e.getMessage());
        } finally {
            rabbitHelper.close(rabbitChannel);
        }
    }

}
