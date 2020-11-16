package com.pacific.messagequeue.service;

import com.pacific.messagequeue.contant.DelayedTimeMap;
import com.pacific.messagequeue.contant.QueueType;
import com.pacific.messagequeue.contant.ServiceTypeMap;
import com.pacific.messagequeue.core.model.BasicQueue;
import com.pacific.messagequeue.core.producer.DelayedExchangeProducer;
import com.pacific.messagequeue.core.producer.OrderedDirectExchangeProducer;
import com.pacific.messagequeue.core.producer.RabbitQueueProducer;
import com.pacific.messagequeue.exception.ExceptionMsgEnum;
import com.pacific.messagequeue.exception.RabbitException;
import com.pacific.messagequeue.model.DelayedMessage;
import com.pacific.messagequeue.model.Message;
import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.model.PersistenceMesssage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author maoxy
 * @date 2019/1/17 15:51
 */
@Service
public class MessageQueueService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageQueueService.class);

    @Value("${rabbit.workqueue.queuename}")
    private String workqueueName;


    @Autowired
    private RabbitQueueProducer rabbitQueueProducer;
    @Autowired
    private OrderedDirectExchangeProducer orderedDirectExchangeProducer;
    @Autowired
    private DelayedExchangeProducer delayedExchangeProducer;
    @Autowired
    private MongoService mongoService;
    /**
     * 发送消息
     * @param msg 消息
     * @return boolean
     */
    public String sendMessage(Message msg) throws RabbitException {
        checkMessageParam(msg);
        Message message = createMessage(msg.getPreId(),msg.getServiceId(),msg.getBusinessId(),msg.getBody(),msg.getCompanyflag());
        LOGGER.info("send message:{},queueType:{},serviceId:{},businessId:{}",message.getBody(),QueueType.WORKQUEUE.getCode(),msg.getServiceId(),msg.getBusinessId());
        PersistenceMesssage persistenceMesssage = persistenceMesssage(message, QueueType.WORKQUEUE.getCode());
        BasicQueue basicQueue = new BasicQueue(workqueueName, true, MessageContantValue.PREFETCH_COUNT, "", workqueueName);
        rabbitQueueProducer.sendTransacitionWorkqueueMessage(persistenceMesssage,basicQueue);
        return message.getGlobalId();
    }

    /**
     * 发送顺序消息
     * @param msg 消息
     * @return boolean
     */
    public String sendOrderedMessage(Message msg) throws RabbitException, IOException, InterruptedException {
        checkMessageParam(msg);
        Message message = createMessage(msg.getPreId(),msg.getServiceId(),msg.getBusinessId(),msg.getBody(),msg.getCompanyflag());
        LOGGER.info("send message:{},queueType:{},serviceId:{},businessId:{}",message.getBody(),QueueType.ORDEREDQUEUE.getCode(),msg.getServiceId(),msg.getBusinessId());
        PersistenceMesssage persistenceMesssage = persistenceMesssage(message, QueueType.ORDEREDQUEUE.getCode());
        String routingKey = getOrderedQueueName(msg.getServiceId());
        orderedDirectExchangeProducer.exchange(persistenceMesssage,routingKey);
        return message.getGlobalId();
    }

    /**
     * 发送延时消息
     * @param delayedMessage 消息
     * @return String
     */
    public String sendDelayedMessage(DelayedMessage delayedMessage) throws RabbitException, IOException, InterruptedException {
        Message msg = new Message();
        BeanUtils.copyProperties(delayedMessage,msg);
        checkMessageParam(msg);
        Message message = createMessage(msg.getPreId(),msg.getServiceId(),msg.getBusinessId(),msg.getBody(),msg.getCompanyflag());
        LOGGER.info("send message:{},queueType:{},serviceId:{},businessId:{}",message.getBody(),QueueType.DELAYEDQUEUE.getCode(),msg.getServiceId(),msg.getBusinessId());
        PersistenceMesssage persistenceMesssage = persistenceMesssage(message, QueueType.DELAYEDQUEUE.getCode());
        String ttl = DelayedTimeMap.getTimeMap().get(delayedMessage.getTimeId());
        delayedExchangeProducer.sendDelayedMessage(persistenceMesssage,MessageContantValue.PRE_DELAYED_DLX_QUEUENAMW+ttl,MessageContantValue.PRE_DELAYED_QUEUENAMW+ttl,ttl);
        return message.getGlobalId();
    }

    private String getOrderedQueueName(Integer serviceId) throws RabbitException {
        String serverName = ServiceTypeMap.getServiceMap().get(serviceId);
        return MessageContantValue.PRE_ORDERED_QUEUENAMW+serverName;
    }

    /**
     * 参数校验
     * @param msg
     */
    private void checkMessageParam(Message msg) throws RabbitException {
        Integer serviceId = msg.getServiceId()==null? 0:msg.getServiceId();
        Integer businessId  = msg.getBusinessId()==null? 0:msg.getBusinessId();
        String data  = msg.getBody()==null? null:msg.getBody();
        if(null == data || serviceId <= 0 || businessId <= 0){
            throw new RabbitException(ExceptionMsgEnum.PARAM_IS_NULL.getCode(),ExceptionMsgEnum.PARAM_IS_NULL.getMsg());
        }
        if (StringUtils.isBlank(ServiceTypeMap.getServiceMap().get(msg.getServiceId()))){
            throw new RabbitException(ExceptionMsgEnum.CANNOT_FOUND_SERVICE.getCode(),ExceptionMsgEnum.CANNOT_FOUND_SERVICE.getMsg());
        }
    }

    /**
     * 构建消息
     * @param serviceId 服务标识
     * @param businessId 业务标识
     * @param data 消息数据
     * @return message 消息
     */
    private Message createMessage(String preId,Integer serviceId, Integer businessId, String data,String companyflag){
        Message message = new Message();
        if (StringUtils.isNotBlank(preId)){
            message.setPreId(preId);
        }
        message.setGlobalId(UUID.randomUUID().toString());
        message.setServiceId(serviceId);
        message.setBusinessId(businessId);
        message.setBody(data);
        message.setCompanyflag(companyflag);
        return message;
    }

    /**
     * 消息持久化到db
     * @param message 消息
     */
    private PersistenceMesssage persistenceMesssage(Message message,Integer queueType){
        LOGGER.info("before send persistence message:{},queueType:{}",message.getGlobalId(),queueType);
        PersistenceMesssage perMessage = new PersistenceMesssage();
        perMessage.setId(message.getGlobalId());
        perMessage.setMessage(message);
        perMessage.setQueueType(queueType);
        perMessage.setIsSend(MessageContantValue.MESSAGE_SEND_NO);
        perMessage.setSendCount(MessageContantValue.INIT_COUNT);
        perMessage.setIsConsume(MessageContantValue.MESSAGE_CONSUME_NO);
        perMessage.setConsumeCount(MessageContantValue.INIT_COUNT);
        perMessage.setIsCallback(MessageContantValue.MESSAGE_CALLBACK_NO);
        perMessage.setCallbackCount(MessageContantValue.INIT_COUNT);
        perMessage.setCreateTime(new Date());
        mongoService.save(perMessage,MessageContantValue.TABLENAME_MESSAGE);
        return perMessage;
    }

}
