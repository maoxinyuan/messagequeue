package com.pacific.messagequeue.contant;

/**
 * @author maoxy
 * @date 2019/1/24 09:41
 */
public enum QueueType {

    /**
     * 工作队列
     */
    WORKQUEUE("workQueue",1001),
    /**
     * 顺序队列
     */
    ORDEREDQUEUE("orderedQueue",1002),
    /**
     * 延时队列
     */
    DELAYEDQUEUE("delayedQueue",1003);

    QueueType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    private String name;

    private Integer code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
