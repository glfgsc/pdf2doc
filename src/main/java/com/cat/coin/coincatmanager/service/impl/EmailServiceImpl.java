package com.cat.coin.coincatmanager.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.cat.coin.coincatmanager.service.EmailService;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("emailService")
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${spring.mail.nickname}")
    private String nickname;

    @Override
    public int sendMail(String email) {
        // 创建一个邮件
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置发件人
        message.setFrom(nickname+'<'+sender+'>');
        // 设置收件人
        message.setTo(email);
        // 设置邮件主题
        message.setSubject("欢迎访问"+nickname);
        //生成六位随机数
        String code = RandomUtil.randomNumbers(6);
        //将验证码存入redis，有效期为5分钟
        redisCacheUtils.setCacheObject("email_code_"+email, code, 300000, TimeUnit.MILLISECONDS);
        String content = "【验证码】您的验证码为：" + code + " 。 验证码五分钟内有效，逾期作废。\n\n\n" +
                "------------------------------\n\n\n" ;
        message.setText(content);
        // 发送邮件
        javaMailSender.send(message);
        return 1;
    }
}
