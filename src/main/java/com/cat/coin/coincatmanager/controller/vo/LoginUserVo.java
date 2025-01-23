package com.cat.coin.coincatmanager.controller.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserVo implements Serializable {
    private String name;
    private String password;

    @Override
    public String toString() {
        return "LoginUserVo{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
