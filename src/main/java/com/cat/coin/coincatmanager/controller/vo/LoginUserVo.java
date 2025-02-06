package com.cat.coin.coincatmanager.controller.vo;

import com.cat.coin.coincatmanager.domain.enums.LoginMethod;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserVo implements Serializable {
    private String name;
    private String password;
    private String email;
    private String code;
    private LoginMethod type;

    @Override
    public String toString() {
        return "LoginUserVo{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", type=" + type +
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

    public LoginMethod getType() {
        return type;
    }

    public void setType(LoginMethod type) {
        this.type = type;
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
