package com.pacific.messagequeue.exception;
/**
 * @author maoxy
 * @date 2019/1/11 16:53
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = -1228269155411585909L;
    private String errmsg;
    private String errcode;

    public BaseException(String errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public BaseException(String errmsg) {
        this.errcode = "-1";
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return this.errcode + "-" + this.errmsg;
    }

    @Override
    public String getMessage() {
        return this.errmsg;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getErrcode() {
        return this.errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

}
    