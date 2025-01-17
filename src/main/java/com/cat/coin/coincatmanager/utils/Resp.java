package com.cat.coin.coincatmanager.utils;


import com.cat.coin.coincatmanager.domain.enums.ExceptionCodeMsg;

public class Resp<T> {
    //状态码
    private int code = 200;
    //状态码信息
    private String msg = "success";
    //返回的数据
    private T data;

    private Resp(int code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Resp success(T data){
        Resp resp = new Resp(200, "success", data);
        return resp;
    }

    public static <T> Resp success(String msg,T data){
        Resp resp = new Resp(200,msg, data);
        return resp;
    }

    public static <T> Resp error(String message){
        Resp resp = new Resp(ExceptionCodeMsg.INTERVAL_ERROR_CODE.getCode(), message, null);
        return resp;
    }

    public static <T> Resp error(int code,String msg){
        Resp resp = new Resp(code,msg, null);
        return resp;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
