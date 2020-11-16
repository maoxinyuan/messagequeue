package com.pacific.messagequeue.controller;

import com.pacific.messagequeue.exception.RabbitException;
import com.pacific.messagequeue.model.DelayedMessage;
import com.pacific.messagequeue.model.Message;
import com.pacific.messagequeue.service.MessageQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;


/**
 * @author maoxy
 * @date 2019/1/11 16:53
 */
@RestController
@RequestMapping("/send")
public class RabbitController {

    Logger logger = LoggerFactory.getLogger(RabbitController.class);

    @Autowired
    private MessageQueueService messageQueueService;

    @PostMapping("workqueue")
    public String sendMessage(@RequestBody Message message) throws RabbitException {
        return messageQueueService.sendMessage(message);
    }

    @PostMapping("ordered")
    public String sendOrderedMessage(@RequestBody Message message) throws RabbitException, IOException, InterruptedException {
        return messageQueueService.sendOrderedMessage(message);
    }

    @PostMapping("delayed")
    public String sendDelayedMessage(@RequestBody DelayedMessage delayedMessage) throws RabbitException, IOException, InterruptedException {
        return messageQueueService.sendDelayedMessage(delayedMessage);
    }

}
