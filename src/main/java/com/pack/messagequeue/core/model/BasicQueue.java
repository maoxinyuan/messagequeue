package com.pacific.messagequeue.core.model;

import com.rabbitmq.client.AMQP;
import lombok.Data;

import java.util.Map;

/**
 * @author maoxy
 * @date 2019/1/18 14:15
 */
@Data
public class BasicQueue {

    /**
     * 队列名
     */
    private String queueName;

    /**
     * 是否持久化
     */
    private boolean durable = false;

    /**
     *
     */
    private boolean exclusive = false;

    /**
     * 自动删除
     */
    private boolean autoDelete = false;

    /**
     * 发送给同一个消费者消息条数
     */
    private Integer prefetchCount;

    /**
     *
     */
    private Map<String, Object> arguments;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 路由key
     */
    private String routingKey;

    /**
     *
     */
    private AMQP.BasicProperties props;

    private boolean autoAck = false;

    public BasicQueue(String queueName,boolean durable){
        this.queueName = queueName;
        this.durable = durable;
    }

    public BasicQueue(String queueName,boolean durable,Integer prefetchCount){
        this.queueName = queueName;
        this.durable = durable;
        this.prefetchCount = prefetchCount;
    }

    public BasicQueue(String queueName,boolean durable,Integer prefetchCount,String exchange,String routingKey){
        this.queueName = queueName;
        this.durable = durable;
        this.prefetchCount = prefetchCount;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }
}
