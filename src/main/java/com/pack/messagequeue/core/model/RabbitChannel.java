package com.pacific.messagequeue.core.model;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author maoxy
 * @date 2019/1/21 16:26
 */
public class RabbitChannel {

    private Integer channelId;

    private Integer connectionId;

    private Channel channel;

    private Boolean isUsed;

    public RabbitChannel(Integer channelId,Integer connectionId,Channel channel, boolean isUsed) {
        this.channelId = channelId;
        this.connectionId = connectionId;
        this.channel = channel;
        this.isUsed = isUsed;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Boolean isUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }
}
