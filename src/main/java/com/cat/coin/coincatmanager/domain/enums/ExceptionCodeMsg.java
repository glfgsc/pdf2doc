package com.cat.coin.coincatmanager.domain.enums;

public enum ExceptionCodeMsg {
    INTERVAL_ERROR_CODE(500,"服务器内部异常");

    private int code ;
    private String msg ;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    ExceptionCodeMsg(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
