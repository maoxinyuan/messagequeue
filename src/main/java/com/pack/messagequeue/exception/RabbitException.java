package com.pacific.messagequeue.exception;

/**
 * @author maoxy
 * @date 2019/2/21 13:38
 */
public class RabbitException extends BaseException{


    public RabbitException(String errcode, String errmsg) {
        super(errcode, errmsg);
    }

    public RabbitException(String errmsg) {
        super(errmsg);
    }
}
