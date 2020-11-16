package com.pacific.messagequeue.model;

import lombok.Data;

import java.util.Date;

/**
 * @author maoxy
 * @date 2019/1/17 17:37
 */
@Data
public class PersistenceMesssage {

    private String id;

    private Message message;

    private Integer queueType;

    private Integer isSend;

    private Integer sendCount;

    private Integer isConsume;

    private Integer consumeCount;

    private Integer isCallback;

    private Integer callbackCount;

    private Date createTime;

}
