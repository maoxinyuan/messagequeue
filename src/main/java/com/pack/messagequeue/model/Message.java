package com.pacific.messagequeue.model;


import lombok.Data;

/**
 * @author maoxy
 * @date 2019/1/17 15:45
 */
@Data
public class Message {
    /**
     * 全局id
     */
    private String globalId;
    /**
     * 上一条消息的id,如果是第一条或不需要保证顺序则为空
     */
    private String preId;
    /**
     * 服务标识
     */
    private Integer serviceId;
    /**
     * 业务标识
     */
    private Integer businessId;
    /**
     * 消息
     */
    private String body;
    /**
     * 公司标签
     */
    private String companyflag;

}
