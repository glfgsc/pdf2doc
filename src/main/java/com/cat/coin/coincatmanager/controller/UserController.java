package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public TokenVo login(@RequestBody LoginUserVo loginUserVo){
        return userService.login(loginUserVo);
    }

    @PostMapping("/register")
    public int register(@RequestBody RegisterUserVo registerUserVo){
        return userService.register(registerUserVo);
    }
}
