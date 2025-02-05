package com.cat.coin.coincatmanager.service;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.domain.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public interface UserService {
    TokenVo login(LoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response);
    void logout();
    Code register(RegisterUserVo registerUserVo,HttpServletRequest request);
}
