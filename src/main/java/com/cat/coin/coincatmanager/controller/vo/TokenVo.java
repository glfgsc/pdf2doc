package com.cat.coin.coincatmanager.controller.vo;

import java.io.Serializable;

public class TokenVo implements Serializable {
    private String token;

    @Override
    public String toString() {
        return "TokenVo{" +
                "token='" + token + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
