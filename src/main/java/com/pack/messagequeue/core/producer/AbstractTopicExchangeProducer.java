package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.ExchangeType;

public abstract class AbstractTopicExchangeProducer extends AbstractExchangeProducer {
    @Override
    public String exchangeType() {
        return ExchangeType.TOPIC;
    }
}
