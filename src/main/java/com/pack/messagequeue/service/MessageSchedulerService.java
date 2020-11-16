package com.pacific.messagequeue.service;

import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.core.model.BasicQueue;
import com.pacific.messagequeue.core.producer.RabbitQueueProducer;
import com.pacific.messagequeue.model.PersistenceMesssage;
import com.pacific.messagequeue.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author maoxy
 * @date 2019/4/16 13:49
 */
@Service
public class MessageSchedulerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageSchedulerService.class);

    @Autowired
    private MongoService mongoService;
    @Autowired
    private MessageHandleService messageHandleService;
    @Autowired
    private RabbitQueueProducer rabbitQueueProducer;

    @Value("${rabbit.workqueue.queuename}")
    private String workqueueName;

    public void workqueueCallbackScheduler(){
        LOGGER.info("开始未回调的消息补偿。。。。。。。。");
        unCallbackMessage(buildParam(MessageContantValue.MESSAGE_SEND_YES,MessageContantValue.MESSAGE_CONSUME_YES,MessageContantValue.MESSAGE_CALLBACK_NO));
        LOGGER.info("回调消息补偿完成。。。。。。。。");
    }

    public void workqueueConsumeScheduler(){
        LOGGER.info("开始未消费的消息补偿。。。。。。。。");
        unConsumer(buildParam(MessageContantValue.MESSAGE_SEND_YES,MessageContantValue.MESSAGE_CONSUME_NO,MessageContantValue.MESSAGE_CALLBACK_NO));
        LOGGER.info("消费消息补偿完成。。。。。。。。");
    }

    public void workqueueSendScheduler(){
        LOGGER.info("开始对发送失败的消息补偿。。。。。。。。");
        unSend(buildParam(MessageContantValue.MESSAGE_SEND_NO,MessageContantValue.MESSAGE_CONSUME_NO,MessageContantValue.MESSAGE_CALLBACK_NO));
        LOGGER.info("发送消息补偿完成。。。。。。。。");
    }

    private void unCallbackMessage(Map<String,Integer> param){
        List<PersistenceMesssage> unCallbackList = mongoService.findList(PersistenceMesssage.class, param,MessageContantValue.PARAMNAME_CALLBACKCOUNT, MessageContantValue.TABLENAME_MESSAGE);
        unCallbackList.stream().forEach(perMessage->{
            LOGGER.info("retry callback message globalId:{}",perMessage.getId());
            try {
                messageHandleService.callback(perMessage.getMessage());
            }catch (Exception e) {
                LOGGER.info(e.getMessage());
            }  finally {
                mongoService.update(perMessage.getId(),MessageContantValue.PARAMNAME_CALLBACKCOUNT,perMessage.getCallbackCount()+1,PersistenceMesssage.class,MessageContantValue.TABLENAME_MESSAGE);
            }
        });
    }

    private void unConsumer(Map<String,Integer> param){
        List<PersistenceMesssage> unConsumeList = mongoService.findList(PersistenceMesssage.class, param,MessageContantValue.PARAMNAME_CONSUMECOUNT, MessageContantValue.TABLENAME_MESSAGE);
        unConsumeList.stream().forEach(perMessage->{
            LOGGER.info("retry consumer message globalId:{}",perMessage.getId());
            try {
                messageHandleService.handleMessage(JsonUtil.toJson(perMessage.getMessage()));
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
            } finally {
                mongoService.update(perMessage.getId(),MessageContantValue.PARAMNAME_CONSUMECOUNT,perMessage.getConsumeCount()+1,PersistenceMesssage.class,MessageContantValue.TABLENAME_MESSAGE);
            }
        });
    }

    private void unSend(Map<String,Integer> param){
        List<PersistenceMesssage> unSendList = mongoService.findList(PersistenceMesssage.class, param,MessageContantValue.PARAMNAME_SENDCOUNT, MessageContantValue.TABLENAME_MESSAGE);
        unSendList.stream().forEach(perMessage->{
            LOGGER.info("retry send message globalId:{}",perMessage.getId());

            try {
                BasicQueue basicQueue = new BasicQueue(workqueueName, true, MessageContantValue.PREFETCH_COUNT, "", workqueueName);
                rabbitQueueProducer.sendTransacitionWorkqueueMessage(perMessage,basicQueue);
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
            }  finally {
                mongoService.update(perMessage.getId(),MessageContantValue.PARAMNAME_SENDCOUNT,perMessage.getSendCount()+1,PersistenceMesssage.class,MessageContantValue.TABLENAME_MESSAGE);
            }

        });
    }

    private Map<String,Integer> buildParam(Integer isSend,Integer isConsume,Integer isCallback){
        Map<String,Integer> param = new HashMap();
        param.put(MessageContantValue.PARAMNAME_ISSEND,isSend);
        param.put(MessageContantValue.PARAMNAME_ISCONSUMER,isConsume);
        param.put(MessageContantValue.PARAMNAME_ISCALLBACK,isCallback);
        return param;
    }

}
