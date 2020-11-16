package com.pacific.messagequeue.initbean;

import com.pacific.messagequeue.contant.MessageContantValue;
import com.pacific.messagequeue.contant.ServiceTypeMap;
import com.pacific.messagequeue.core.consumer.OrderedExchangeConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class OrderedQueueConsumer implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(OrderedQueueConsumer.class);
    @Value("${rabbit.orderedqueue.consumercount}")
    private Integer orderedConsumerCount = 3;

    @Autowired
    private OrderedExchangeConsumer orderedExchangeConsumer;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("------------ OrderedQueue start batch register consumer...");
        Map<Integer, String> serviceMap = ServiceTypeMap.getServiceMap();
        serviceMap.forEach((k,v)->{
            createConsumer(v);
        });
    }

    public void updateConsumer(Collection<String> oldValues,Collection<String> newValues){
        newValues.stream().forEach(serverName->{
            if (!oldValues.contains(serverName)){
                for (int i = 0;i<orderedConsumerCount;i++){
                    createConsumer(serverName);
                }
            }
        });
    }

    private void createConsumer(String value){
        String orderedQueueName = MessageContantValue.PRE_ORDERED_QUEUENAMW+value;
        try {
            orderedExchangeConsumer.createConsumer(orderedQueueName,orderedQueueName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
