package com.pacific.messagequeue.contant;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.pacific.messagequeue.initbean.OrderedQueueConsumer;
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
public class ServiceTypeMap {

    private static Logger LOGGER = LoggerFactory.getLogger(ServiceTypeMap.class);

    private static Map<Integer,String> serviceMap;
    @Autowired
    private OrderedQueueConsumer orderedQueueConsumer;
    private ServiceTypeMap(){
        serviceMap = new HashMap<Integer,String>(1);
        init();
    }

    private void init(){
        Config config = ConfigService.getConfig("callback");
        Set<String> propertyNames = config.getPropertyNames();
        putValues(propertyNames,config);
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                Set<String> oldValues = new HashSet<>();
                serviceMap.values().forEach((v)->{
                    oldValues.add(v);
                });
                Set<String>  changedKeys= changeEvent.changedKeys();
                LOGGER.info("message ServiceTypeMap has changed");
                putValues(changedKeys,config);
                Collection<String> newValues = serviceMap.values();
                orderedQueueConsumer.updateConsumer(oldValues, newValues);
            }
        });

    }
    private void putValues(Set<String> propertyNames, Config config){
        Iterator<String> it  = propertyNames.iterator();
        while (it.hasNext()) {
            String next = it.next();
            Integer value = config.getIntProperty(next, null);
            serviceMap.put(value,next);
            LOGGER.info("message ServiceTypeMap put key:{},value:{}",value,next);
        }
    }

    public static Map<Integer,String> getServiceMap(){
        return serviceMap;
    }
}
