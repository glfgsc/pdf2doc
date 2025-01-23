package com.cat.coin.coincatmanager.service;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.User;

import java.util.HashMap;

public interface UserService {
    TokenVo login(LoginUserVo loginUserVo);
    void logout();
    int register(RegisterUserVo registerUserVo);
}
