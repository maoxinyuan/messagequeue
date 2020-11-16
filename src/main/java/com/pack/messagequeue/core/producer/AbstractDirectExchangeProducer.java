package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.ExchangeType;

public abstract class AbstractDirectExchangeProducer extends AbstractExchangeProducer {
    @Override
    public String exchangeType() {
        return ExchangeType.DIRECT;
    }
}
