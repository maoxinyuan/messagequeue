package com.pacific.messagequeue.model;

import lombok.Data;

@Data
public class DelayedMessage extends Message {
    private Integer timeId;
}
