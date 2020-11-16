package com.pacific.messagequeue.core.model;

import com.rabbitmq.client.Connection;

/**
 * @author maoxy
 * @date 2019/1/21 16:26
 */
public class RabbitConnection {

    private Integer connectionId;

    private Connection connection;

    private Integer channelCount;


    public RabbitConnection(Integer connectionId,Connection connection, Integer channelCount) {
        this.connectionId = connectionId;
        this.connection = connection;
        this.channelCount = channelCount;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Integer getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
    }

}
