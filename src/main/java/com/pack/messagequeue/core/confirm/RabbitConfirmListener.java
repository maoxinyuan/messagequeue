package com.pacific.messagequeue.core.confirm;

import com.pacific.messagequeue.config.SpringContext;
import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.model.Message;
import com.pacific.messagequeue.model.PersistenceMesssage;
import com.pacific.messagequeue.service.MongoService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author maoxy
 * @date 2019/1/28 11:07
 */
public class RabbitConfirmListener implements ConfirmListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitConfirmListener.class);

    private MongoService mongoService;

    private PersistenceMesssage persistenceMesssage;

    private Channel channel;

    public RabbitConfirmListener(PersistenceMesssage persistenceMesssage,Channel channel){
        this.persistenceMesssage = persistenceMesssage;
        this.mongoService = SpringContext.getBean(MongoService.class);
        this.channel = channel;
    }

    @Override
    public void handleAck(long deliveryTag, boolean multiple) throws IOException {
        LOGGER.info("message send success,deliveryTag:{},multiple:{}",deliveryTag,multiple);
        mongoService.update(persistenceMesssage.getId(), MessageContantValue.PARAMNAME_ISSEND, MessageContantValue.MESSAGE_SEND_YES, PersistenceMesssage.class,MessageContantValue.TABLENAME_MESSAGE);
    }
    @Override
    public void handleNack(long deliveryTag, boolean multiple) throws IOException {
        LOGGER.info("message send fail,deliveryTag:{},multiple:{}",deliveryTag,multiple);
    }

}
