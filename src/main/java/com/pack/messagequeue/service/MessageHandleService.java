package com.pacific.messagequeue.service;

import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.contant.QueueType;
import com.pacific.messagequeue.contant.ServiceTypeMap;
import com.pacific.messagequeue.model.Message;
import com.pacific.messagequeue.model.PersistenceMesssage;
import com.pacific.messagequeue.utils.JsonUtil;
import com.pacific.messagequeue.utils.HttpRequestService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author maoxy
 * @date 2019/1/17 17:59
 */
@Service("messageHandleService")
public class MessageHandleService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageHandleService.class);
    private final static long sleepTime = 50;
    private final static long waitTime = 3000;

    @Autowired
    private MongoService mongoService;

    @Value("${mq.workqueue.callback.uri}")
    private String uri;

    @Autowired
    HttpRequestService httpRequestService;

    /**
     * 处理消息
     * @param messageBody 消息体
     */
    public boolean handleMessage(String messageBody){
        PersistenceMesssage persistenceMesssage = JsonUtil.toObject(messageBody, PersistenceMesssage.class);
        Message message = persistenceMesssage.getMessage();
        LOGGER.info("==================start handle receive message GlobalId:"+message.getGlobalId());
        updateMessageConsumeStatus(message);
        long startTime = System.currentTimeMillis();
        return callback(persistenceMesssage,startTime);
    }

    /**
     * 回调处理
     * @param persistenceMesssage  消息
     * @param startTime  开始时间
     * @return  boolean
     */
    private boolean callback(PersistenceMesssage persistenceMesssage,long startTime){
        Message message = persistenceMesssage.getMessage();
        Boolean result = false;
        try {
            long time = System.currentTimeMillis()-startTime;
            if (time>=waitTime){
                return false;
            }
            if (QueueType.ORDEREDQUEUE.getCode().equals(persistenceMesssage.getQueueType()) && StringUtils.isNotEmpty(message.getPreId())){
                LOGGER.info("globalId:{},preId:{}",message.getGlobalId(),message.getPreId());
                if (time<sleepTime){
                    if (!checkPreMessage(message.getPreId())){
                        updateMessageAbandon(message);
                        return false;
                    }
                }
                if (!checkPreMessageStatus(message.getPreId())){
                    Thread.sleep(sleepTime);
                    LOGGER.info("====================callback,globalId:{},callback",message.getGlobalId());
                    return callback(persistenceMesssage,startTime);
                }
            }
            callback(message);
            LOGGER.info("====================callback,globalId:{}",message.getGlobalId());
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            return false;



            
        }
        return updateMessageCallbackStatus(message);
    }

    /**
     * 修改消息的消费状态
     * @param message 消息
     */
    private void updateMessageConsumeStatus(Message message){
        LOGGER.info("==================update message consume status GlobalId:"+message.getGlobalId());
        mongoService.update(message.getGlobalId(),MessageContantValue.PARAMNAME_ISCONSUMER,MessageContantValue.MESSAGE_CONSUME_YES,PersistenceMesssage.class,MessageContantValue.TABLENAME_MESSAGE);
    }

    /**
     *  回调业务
     * @param message
     * @return boolean
     */
    public boolean callback(Message message){
        LOGGER.info("==================callback message GlobalId:{}",message.getGlobalId());
        String serviceName = ServiceTypeMap.getServiceMap().get(message.getServiceId());
        if (StringUtils.isBlank(serviceName)){
            LOGGER.info("serviceName is null:{}",message.getServiceId());
            return false;
        }
        LOGGER.info("==================callback serviceName :{},globalId:{}",serviceName,message.getGlobalId());
        String url = serviceName + uri;
        Boolean result = httpRequestService.post(url, message, Boolean.class,message.getCompanyflag());
        return result;
    }
    /**
     * 修改消息的回调状态
     * @param message 消息
     * @return  boolean
     */
    private boolean updateMessageCallbackStatus(Message message){
        LOGGER.info("==================handle message callback status GlobalId:"+message.getGlobalId());
        PersistenceMesssage persistenceMesssage = mongoService.find(message.getGlobalId(), PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE);
        if (persistenceMesssage != null){
            persistenceMesssage.setIsCallback(MessageContantValue.MESSAGE_CALLBACK_YES);
            mongoService.delete(message.getGlobalId(),PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE);
            mongoService.save(persistenceMesssage,MessageContantValue.TABLENAME_MESSAGE_BAK);
        }
        return true;
    }

    /**
     * 将无用消息移除至废弃表
     * @param message  消息
     */
    private void updateMessageAbandon(Message message){
        LOGGER.info("==================handle message is abandon GlobalId:"+message.getGlobalId());
        PersistenceMesssage persistenceMesssage = mongoService.find(message.getGlobalId(), PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE);
        if (persistenceMesssage != null){
            mongoService.delete(message.getGlobalId(),PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE);
            mongoService.save(persistenceMesssage,MessageContantValue.TABLENAME_MESSAGE_ABANDON);
        }
    }

    /**
     * 校验父消息是否存在
     * @param preId  父id
     * @return  boolean
     */
    private boolean checkPreMessage(String preId){
        PersistenceMesssage persistenceMesssage = mongoService.find(preId, PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE_BAK);
        if (persistenceMesssage == null){
            persistenceMesssage = mongoService.find(preId, PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE);
            if (persistenceMesssage == null){
                return false;
            }
        }
        return true;
    }

    /**
     * 校验父消息是否回调
     * @param preId  父id
     * @return  boolean
     */
    private boolean checkPreMessageStatus(String preId){
        PersistenceMesssage persistenceMesssage = mongoService.find(preId, PersistenceMesssage.class, MessageContantValue.TABLENAME_MESSAGE_BAK);
        if (persistenceMesssage != null && persistenceMesssage.getIsCallback() == 1){
            LOGGER.info("golbalId:{},true",persistenceMesssage.getId());
            return true;
        }
        LOGGER.info("parent callback staute:false");
        return false;
    }
}
