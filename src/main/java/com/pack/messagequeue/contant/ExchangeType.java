package com.pacific.messagequeue.contant;

/**
 * @author maoxy
 * @date 2019/7/5 09:41
 * 交换机的类型
 */
public class ExchangeType {

    /**
     * 路由
     */
    public final static String DIRECT = "direct";
    /**
     * 主题
     */
    public final static String TOPIC = "topic";
    /**
     *
     */
    public final static String HEADERS = "headers";
    /**
     * 发布订阅
     */
    public final static String FANOUT= "fanout";

}
