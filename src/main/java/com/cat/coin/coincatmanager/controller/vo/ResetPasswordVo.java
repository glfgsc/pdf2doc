package com.cat.coin.coincatmanager.controller.vo;

import java.io.Serializable;

public class ResetPasswordVo implements Serializable {
    private String email;
    private String code;
    private String password;

    @Override
    public String toString() {
        return "ResetPasswordVo{" +
                "email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
