package com.pacific.messagequeue.contant;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.pacific.messagequeue.initbean.DelayedQueueConsumer;
import com.pacific.messagequeue.initbean.OrderedQueueConsumer;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author maoxy
 * @date 2019/1/30 09:59
 */
@Component
public class DelayedTimeMap {

    private static Logger LOGGER = LoggerFactory.getLogger(DelayedTimeMap.class);

    private static Map<Integer,String> timeMap;
    @Autowired
    private DelayedQueueConsumer delayedQueueConsumer;
    private DelayedTimeMap(){
        timeMap = new HashMap<Integer,String>(1);
        init();
    }

    private void init(){
        Config config = ConfigService.getConfig("delayedtime");
        Set<String> propertyNames = config.getPropertyNames();
        putValues(propertyNames,config);
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                Set<String> oldValues = new HashSet<>();
                timeMap.values().forEach((v)->{
                    oldValues.add(v);
                });
                Set<String>  changedKeys= changeEvent.changedKeys();
                LOGGER.info("message delayedtime is changing");
                putValues(changedKeys,config);
                Collection<String> newValues = timeMap.values();
                delayedQueueConsumer.updateConsumer(oldValues, newValues);
            }
        });

    }
    private void putValues(Set<String> propertyNames, Config config){
        Iterator<String> it  = propertyNames.iterator();
        while (it.hasNext()) {
            String next = it.next();
            Integer value = config.getIntProperty(next, null);
            timeMap.put(value,next);
            LOGGER.info("message timeMap put key:{},value:{}",value,next);
        }
    }

    public static Map<Integer,String> getTimeMap(){
        return timeMap;
    }
}
