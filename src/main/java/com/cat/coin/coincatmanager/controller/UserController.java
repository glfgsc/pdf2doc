package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.ResetPasswordVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.domain.pojo.User;
import com.cat.coin.coincatmanager.service.MailService;
import com.cat.coin.coincatmanager.service.UserService;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import com.cat.coin.coincatmanager.utils.VerificationCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private RedisCacheUtils redisCacheUtils;


    @PostMapping("/password/login")
    public AjaxResult passwordLogin(@RequestBody LoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response){
        return userService.passwordLogin(loginUserVo,request,response);
    }

    @GetMapping("/logout")
    public void logout(){
        userService.logout();
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterUserVo registerUserVo,HttpServletRequest request){
        return userService.register(registerUserVo,request);
    }

    @PostMapping("/resetPassword")
    public AjaxResult resetPassword(@RequestBody ResetPasswordVo resetPasswordVo, HttpServletRequest request){
        return userService.resetPassword(resetPasswordVo);
    }

    @GetMapping("/sendLoginMail")
    public AjaxResult sendLoginMail(@RequestParam String email,HttpServletRequest request) throws MessagingException {
        return userService.sendLoginMail(email);
    }

    @GetMapping("/sendRegisterMail")
    public AjaxResult sendRegisterMail(@RequestParam String email,HttpServletRequest request) throws MessagingException {
        return userService.sendRegisterMail(email);
    }

    @GetMapping("/sendForgetPasswordMail")
    public AjaxResult sendPasswordMail(@RequestParam String email,HttpServletRequest request) throws MessagingException {
        return userService.sendForgetPasswordMail(email);
    }

    @GetMapping("/checkUserName")
    public AjaxResult checkUserName(String name){
        return userService.checkUserName(name);
    }
}
