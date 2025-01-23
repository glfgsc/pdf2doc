package com.cat.coin.coincatmanager.controller.vo;

import java.io.Serializable;

public class RegisterUserVo implements Serializable {
    private String name;
    private String password;
    private String email;

    @Override
    public String toString() {
        return "RegisterUserVo{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
