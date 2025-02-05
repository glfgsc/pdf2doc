package com.cat.coin.coincatmanager.service.impl;

import com.cat.coin.coincatmanager.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service("mailService")
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Override
    public void sendMail(String to, String subject, String content) throws MessagingException {
        // 创建邮件消息
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(new InternetAddress("icoincat"+" <"+from+">"));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        // 发送邮件
        mailSender.send(message);
    }
}
