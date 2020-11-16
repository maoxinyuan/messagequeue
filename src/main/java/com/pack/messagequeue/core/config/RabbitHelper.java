package com.pacific.messagequeue.core.config;

import com.pacific.messagequeue.config.RabbitConfig;
import com.pacific.messagequeue.config.SpringContext;
import com.pacific.messagequeue.core.model.RabbitChannel;
import com.pacific.messagequeue.core.model.RabbitConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author maoxy
 * @date 2019/1/21 14:21
 */
@Component
public class RabbitHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitHelper.class);

    private RabbitConfig rabbitConfig;
    /**
     * 所有连接队列
     */
    private Map<Integer,RabbitConnection> connectionMap;

    /**
     * 所有信道
     */
    private Map<Integer,RabbitChannel> channelMap;
    /**
     * 连接池初始化大小
     */
    private static final int INIT_CONNECTION_SIZE = 2;
    /**
     * 连接池的最大值
     */
    private static final int MAX_CONNECTION_SIZE = 50;
    private static final int CLOSE_CONNECTION_SIZE = 20;

    /**
     * 信道初始化大小
     */
    private static final int INIT_CHANNEL_SIZE = 10;
    private static final int CLOSE_CHANNEL_SIZE = 200;
    /**
     * 每个连接信道的最大值
     */
    private static final int MAX_CHANNEL_SIZE = 15;

    private AtomicInteger atomicConnectionId = new AtomicInteger(0);
    private AtomicInteger atomicChannelId = new AtomicInteger(0);

    public RabbitHelper() throws IOException, TimeoutException {
        rabbitConfig = SpringContext.getBean(RabbitConfig.class);
        this.initConnection();
        this.initChannel();
    }

    /**
     * 初始化连接
     * @throws IOException
     * @throws TimeoutException
     */
    private void initConnection() throws IOException, TimeoutException {
        LOGGER.info("init connection start......");
        if(null == connectionMap) {
            //创建rabbitmq连接池
            connectionMap = new ConcurrentHashMap<Integer,RabbitConnection>();
            //循环创建数据库连接
            for (int i = 0; i < INIT_CONNECTION_SIZE; i++) {
                Integer connectionId = atomicConnectionId.getAndIncrement();
                Connection connection = rabbitConfig.getConnection();
                RabbitConnection rabbitConnection = new RabbitConnection(connectionId,connection,0);
                connectionMap.put(connectionId,rabbitConnection);
                LOGGER.info("connectionId:{}",connectionId);
            }
        }
        LOGGER.info("init connection end......");
    }

    /**
     * 初始化信道
     * @throws IOException
     */
    private void initChannel() throws IOException {
        LOGGER.info("init channel start......");
        if(null == channelMap) {
            //创建rabbitmq连接池
            channelMap = new ConcurrentHashMap<Integer, RabbitChannel>();
            //循环创建数据库连接
            for (int i = 0; i < INIT_CHANNEL_SIZE; i++) {
                Integer channelId = atomicChannelId.getAndIncrement();
                RabbitConnection rabbitConnection = getRabbitConnection();
                Channel channel = rabbitConnection.getConnection().createChannel();
                RabbitChannel rabbitChannel = new RabbitChannel(channelId,rabbitConnection.getConnectionId(),channel,false);
                LOGGER.info("channelId:{}",channelId);
                channelMap.put(channelId,rabbitChannel);
                rabbitConnection.setChannelCount(rabbitConnection.getChannelCount()+1);
                connectionMap.put(rabbitConnection.getConnectionId(),rabbitConnection);
            }
        }
        LOGGER.info("init channel end......");
    }

    /**
     * 获取连接
     * @return
     */
    public RabbitConnection getRabbitConnection() {
        LOGGER.info("get connection stsrt......");
        RabbitConnection rabbitConnection = null;
        //循环查找空闲的连接，直到找到位置
        synchronized (connectionMap) {
            Iterator<Integer> iterator = connectionMap.keySet().iterator();
            while (iterator.hasNext()){
                Integer connectionId = iterator.next();
                if (connectionMap.get(connectionId).getChannelCount() < MAX_CHANNEL_SIZE) {
                    rabbitConnection = connectionMap.get(connectionId);
                    LOGGER.info("get old connection success,connectionId:{}", rabbitConnection.getConnectionId());
                    return rabbitConnection;
                }
            }
            //如果没有找到空闲的连接，则创建连接
            if (connectionMap.size() < MAX_CONNECTION_SIZE) {
                try {
                    Integer connectionId = atomicConnectionId.getAndIncrement();
                    Connection connection = rabbitConfig.getConnection();
                    rabbitConnection = new RabbitConnection(connectionId, connection, 0);
                    connectionMap.put(connectionId, rabbitConnection);
                    LOGGER.info("get new connection success:{},connectionId:{}", rabbitConnection, connectionId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                return rabbitConnection;
                //如果连接池的大小达到了最大连接数,进行等待，知道有链接进入空闲状态
            } else if (connectionMap.size() == MAX_CONNECTION_SIZE) {
                LOGGER.info("connection count is max,wait...");
                try {
                    connectionMap.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return rabbitConnection;
        }
    }

    /**
     * 获取信道
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public RabbitChannel getRabbitChannel() throws IOException, InterruptedException {
        synchronized (connectionMap) {
            RabbitChannel rabbitChannel = null;
            Iterator<Integer> iterator = channelMap.keySet().iterator();
            while (iterator.hasNext()){
                Integer channelId = iterator.next();
                rabbitChannel = channelMap.get(channelId);
                if (!rabbitChannel.isUsed()) {
                    rabbitChannel.setUsed(true);
                    channelMap.put(channelId, rabbitChannel);
                    LOGGER.info("get old channel success,channelId:{}", rabbitChannel.getChannelId());
                    return rabbitChannel;
                }
            }
            RabbitConnection rabbitConnection = getRabbitConnection();
            if (rabbitConnection.getChannelCount() < MAX_CHANNEL_SIZE) {
                Integer channelId = atomicChannelId.getAndIncrement();
                Channel channel = rabbitConnection.getConnection().createChannel();
                rabbitChannel = new RabbitChannel(channelId, rabbitConnection.getConnectionId(), channel, true);
                LOGGER.info("get new channel success:{},channelId:{}", rabbitChannel, channelId);
                channelMap.put(channelId, rabbitChannel);
                rabbitConnection.setChannelCount(rabbitConnection.getChannelCount() + 1);
                connectionMap.put(rabbitConnection.getConnectionId(), rabbitConnection);
                return rabbitChannel;
            }
            return null;
        }
    }

    /**
     * 关闭连接信道
     * @param rabbitChannel 信道
     */
    public void close(RabbitChannel rabbitChannel){
        try {

            Integer connectionId = rabbitChannel.getConnectionId();
            RabbitConnection rabbitConnection = connectionMap.get(connectionId);
            synchronized(channelMap){
                LOGGER.info("close rabbitChannel:{},channelId:{}",rabbitChannel,rabbitChannel.getChannelId());
                if (channelMap.size() > CLOSE_CHANNEL_SIZE){
                    channelMap.remove(rabbitChannel.getChannelId());
                    Channel channel = rabbitChannel.getChannel();
                    if (channel.isOpen()){
                        channel.close();
                    }
                    rabbitConnection.setChannelCount(rabbitConnection.getChannelCount()-1);
                }else {
                    rabbitChannel.setUsed(false);
                    LOGGER.info("update channel status id:{}",rabbitChannel.getChannelId());
                    channelMap.put(rabbitChannel.getChannelId(),rabbitChannel);
                }
            }
            synchronized(connectionMap){
                LOGGER.info("close rabbitConnection:{},connectionId:{}",rabbitConnection,rabbitChannel.getConnectionId());
                if (connectionMap.size() > CLOSE_CONNECTION_SIZE && rabbitConnection.getChannelCount()<=0){
                    connectionMap.remove(rabbitConnection.getConnectionId());
                    LOGGER.info("close connection id:{}",rabbitConnection.getConnectionId());
                    Connection connection = rabbitConnection.getConnection();
                    if ( connection.isOpen()) {
                        connection.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
