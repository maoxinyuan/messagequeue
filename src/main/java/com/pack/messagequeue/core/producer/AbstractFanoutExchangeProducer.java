package com.pacific.messagequeue.core.producer;

import com.pacific.messagequeue.contant.ExchangeType;

public abstract class AbstractFanoutExchangeProducer extends AbstractExchangeProducer {
    @Override
    public String exchangeType() {
        return ExchangeType.FANOUT;
    }
}
