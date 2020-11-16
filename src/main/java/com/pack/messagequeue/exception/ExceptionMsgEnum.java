package com.pacific.messagequeue.exception;
/**
 * @author maoxy
 * @date 2019/1/11 16:53
 */
public enum ExceptionMsgEnum {
    /**
     * 100001
     */
    PARAM_IS_NULL("param cannot be null","100001"),
    /**
     * 100002
     */
    EUREKA_SERVICE("this serviceId cannot find service in eureka","100002"),
    /**
     * 100003
     */
    CANNOT_FOUND_SERVICE("this serviceId cannot find serviceName in apollo config","100003");

    ExceptionMsgEnum(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    private String msg;

    private String code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
