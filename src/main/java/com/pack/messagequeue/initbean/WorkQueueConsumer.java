package com.pacific.messagequeue.initbean;

import com.pacific.messagequeue.core.consumer.WorkNormalConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class WorkQueueConsumer implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(WorkQueueConsumer.class);

    @Value("${rabbit.workqueue.consumercount}")
    private Integer workqueueCusumerCount;
    @Value("${rabbit.workqueue.queuename}")
    private String workqueueName;
    @Autowired
    private WorkNormalConsumer rabbitQueueConsumer;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("------------ start batch register consumer...");
        try {
            for (int i = 0;i<workqueueCusumerCount;i++){
                rabbitQueueConsumer.createConsumer(workqueueName);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        } catch (TimeoutException e) {
            logger.info(e.getMessage());
        }
    }
}
