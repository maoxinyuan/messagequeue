package com.pacific.messagequeue.config;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author maoxy
 * @date 2019/1/21 14:47
 */
@Component
public class RabbitConfig {
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${rabbitmq.virtual_host}")
    private String virtualHost;
    @Value("${rabbitmq.connectiontimeout}")
    private int connectionTimeout;
    @Value("${rabbitmq.addresses}")
    private String addresses;


    @Bean
    public ConnectionFactory connectionFactory(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setConnectionTimeout(connectionTimeout);
        return factory;
    }

    public Connection getConnection() throws IOException, TimeoutException {
        String[] addressArr = addresses.split(",");
        List<Address> addressList = new ArrayList<Address>();
        Arrays.asList(addressArr).forEach(address->{
            String host = address.substring(0, address.indexOf(":"));
            String port = address.substring(address.indexOf(":") + 1);
            addressList.add(new Address(host,Integer.valueOf(port)));
        });
        Connection connection = connectionFactory().newConnection(addressList);
        return connection;
    }
}
