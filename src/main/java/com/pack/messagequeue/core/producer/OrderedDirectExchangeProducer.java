package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.MessageContantValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderedDirectExchangeProducer extends AbstractDirectExchangeProducer {

    @Value("${rabbit.ordered.exchangename}")
    private String exchangeName;

    @Override
    public String exchangeName() {
        return exchangeName;
    }

    @Override
    public int basicQos() {
        return MessageContantValue.PREFETCH_COUNT;
    }

    @Override
    public boolean isDurable() {
        return true;
    }
}
