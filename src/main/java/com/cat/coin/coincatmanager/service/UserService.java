package com.cat.coin.coincatmanager.service;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.ResetPasswordVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.domain.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public interface UserService extends UserDetailsService {
    AjaxResult passwordLogin(LoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response);
    void logout();
    AjaxResult register(RegisterUserVo registerUserVo,HttpServletRequest request);
    UserDetails loadUserByEmail(String email);
    @Override
    UserDetails loadUserByUsername(String username);
    AjaxResult checkUserName(String name);
    AjaxResult sendLoginMail(String email) throws MessagingException;
    AjaxResult sendRegisterMail(String email) throws MessagingException;
    AjaxResult sendForgetPasswordMail(String email) throws MessagingException;
    AjaxResult resetPassword(ResetPasswordVo resetPasswordVo);
}
