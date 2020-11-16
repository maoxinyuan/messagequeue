package com.pacific.messagequeue.controller;

import com.pacific.messagequeue.service.MessageSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maoxy
 * @date 2019/4/16 13:25
 */
@RestController
@RequestMapping("/api/scheduler")
public class MessageSchedulerController {

    @Autowired
    private MessageSchedulerService messageSchedulerService;

    @PostMapping("/workqueue/send")
    public void workqueueSendScheduler(){
        messageSchedulerService.workqueueSendScheduler();
    }
    @PostMapping("/workqueue/consume")
    public void workqueueConsumeScheduler(){
        messageSchedulerService.workqueueConsumeScheduler();
    }
    @PostMapping("/workqueue/callback")
    public void workqueueCallbackScheduler(){
        messageSchedulerService.workqueueCallbackScheduler();
    }

}
