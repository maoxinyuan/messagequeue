package com.pacific.messagequeue.contant;

/**
 * @author maoxy
 * @date 2019/1/17 17:43
 */
public class MessageContantValue {

    /**
     * 消息未发送
     */
    public final static Integer MESSAGE_SEND_NO = 0;
    /**
     * 消息已发送
     */
    public final static Integer MESSAGE_SEND_YES = 1;
    /**
     * 消息未消费
     */
    public final static Integer MESSAGE_CONSUME_NO = 0;
    /**
     * 消息已消费
     */
    public final static Integer MESSAGE_CONSUME_YES = 1;
    /**
     * 消息未回调
     */
    public final static Integer MESSAGE_CALLBACK_NO = 0;
    /**
     * 消息已回调
     */
    public final static Integer MESSAGE_CALLBACK_YES = 1;

    /**
     * isSend参数名
     */
    public final static String PARAMNAME_ISSEND = "isSend";
    /**
     * isConsume参数名
     */
    public final static String PARAMNAME_ISCONSUMER = "isConsume";
    /**
     * isCallback参数名
     */
    public final static String PARAMNAME_ISCALLBACK = "isCallback";
    /**
     * sendCount参数名
     */
    public final static String PARAMNAME_SENDCOUNT = "sendCount";
    /**
     * consumeCount参数名
     */
    public final static String PARAMNAME_CONSUMECOUNT = "consumeCount";
    /**
     * callbackCount参数名
     */
    public final static String PARAMNAME_CALLBACKCOUNT = "callbackCount";


    /**
     * 初始化大小
     */
    public final static Integer INIT_COUNT = 0;
    /**
     * 补偿次数
     */
    public final static Integer SCHEDULER_COUNT = 3;

    /**
     * mongodb 消息备份表
     */
    public final static String TABLENAME_MESSAGE = "messages";
    public final static String TABLENAME_MESSAGE_BAK = "messages_bak";
    public final static String TABLENAME_MESSAGE_ABANDON = "messages_abandon";

    public final static Integer PREFETCH_COUNT= 1;
    /**
     * 顺序队列前缀
     */
    public final static String PRE_ORDERED_QUEUENAMW= "ordered_";
    /**
     * 延时队列前缀
     */
    public final static String PRE_DELAYED_QUEUENAMW= "delayed_";
    /**
     * 延时死信队列前缀
     */
    public final static String PRE_DELAYED_DLX_QUEUENAMW= "delayed_dlx_";

    /**
     * 过期消息发送到指定exchange中
     */
    public final static String ARGS_X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    /**
     * 消息过期时间
     */
    public final static String ARGS_X_MESSAGE_TTL = "x-message-ttl";

}
