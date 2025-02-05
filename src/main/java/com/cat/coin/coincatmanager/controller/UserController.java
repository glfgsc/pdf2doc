package com.cat.coin.coincatmanager.controller;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.service.MailService;
import com.cat.coin.coincatmanager.service.UserService;
import com.cat.coin.coincatmanager.utils.VerificationCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @PostMapping("/login")
    public TokenVo login(@RequestBody LoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response){
        return userService.login(loginUserVo,request,response);
    }

    @PostMapping("/register")
    public Code register(@RequestBody RegisterUserVo registerUserVo,HttpServletRequest request){
        return userService.register(registerUserVo,request);
    }

    @GetMapping("/sendMail")
    public void sendMail(@RequestParam String email,HttpServletRequest request) throws MessagingException {
        // 生成验证码
        String code = VerificationCodeUtils.generateCode(6);
        request.getSession().setAttribute("code", code);
        // 发送邮件
        String subject = "注册验证码";
        String content = "尊敬的用户，您的验证码为：" + code;
        mailService.sendMail(email,subject,content);
    }
}
