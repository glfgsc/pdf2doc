package com.cat.coin.coincatmanager.domain.pojo;


import lombok.Data;

@Data
public class Code {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public Code(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

}

