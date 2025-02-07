package com.cat.coin.coincatmanager.service.impl;

import com.cat.coin.coincatmanager.config.EmailAuthenticationToken;
import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.ResetPasswordVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.enums.GlobalCodeConstants;
import com.cat.coin.coincatmanager.domain.enums.LoginMethod;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.domain.pojo.SecurityUser;
import com.cat.coin.coincatmanager.domain.pojo.User;
import com.cat.coin.coincatmanager.mapper.UserMapper;
import com.cat.coin.coincatmanager.service.MailService;
import com.cat.coin.coincatmanager.service.UserService;
import com.cat.coin.coincatmanager.utils.JwtUtils;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import com.cat.coin.coincatmanager.utils.VerificationCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userMapper.getUserByName(username);
        // 在这里将用户的权限转换为字符串数组
        List<String> list = new ArrayList<>(Arrays.asList(user.getRole()));
        return new SecurityUser(user,list);
    }

    @Override
    public AjaxResult checkUserName(String name) {
        User user = userMapper.getUserByName(name);
        if(user == null){
            return AjaxResult.success(1);
        }
        return AjaxResult.success(0);
    }

    @Override
    public AjaxResult sendLoginMail(String email) throws MessagingException {
        User user = userMapper.getUserByEmail(email);
        if(user == null){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_NOT_EXIST.getCode(),GlobalCodeConstants.EMAIL_NOT_EXIST.getMsg());
        }
        // 生成验证码
        String code = VerificationCodeUtils.generateCode(6);
        String redisKey = "loginVerifyCode:" + email;
        redisCacheUtils.setCacheObject(redisKey,code,60000, TimeUnit.MILLISECONDS);
        // 发送邮件
        String subject = "登录验证码";
        String content = "尊敬的用户，您的验证码为：" + code + ",有效期为1分钟";
        mailService.sendMail(email,subject,content);
        return AjaxResult.success(1);
    }

    @Override
    public AjaxResult sendRegisterMail(String email) throws MessagingException {
        // 生成验证码
        String code = VerificationCodeUtils.generateCode(6);
        String redisKey = "registerVerifyCode:" + email;
        redisCacheUtils.setCacheObject(redisKey,code,60000, TimeUnit.MILLISECONDS);
        // 发送邮件
        String subject = "注册验证码";
        String content = "尊敬的用户，您的验证码为：" + code + ",有效期为1分钟";
        mailService.sendMail(email,subject,content);
        return AjaxResult.success(1);
    }

    @Override
    public AjaxResult sendForgetPasswordMail(String email) throws MessagingException {
        User user = userMapper.getUserByEmail(email);
        if(user == null){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_NOT_EXIST.getCode(),GlobalCodeConstants.EMAIL_NOT_EXIST.getMsg());
        }
        // 生成验证码
        String code = VerificationCodeUtils.generateCode(6);
        String redisKey = "forgetPasswordVerifyCode:" + email;
        redisCacheUtils.setCacheObject(redisKey,code,60000, TimeUnit.MILLISECONDS);
        // 发送邮件
        String subject = "重置密码验证码";
        String content = "尊敬的用户，您的验证码为：" + code + ",有效期为1分钟";
        mailService.sendMail(email,subject,content);
        return AjaxResult.success(1);
    }

    @Override
    public AjaxResult resetPassword(ResetPasswordVo resetPasswordVo) {
        User user = userMapper.getUserByEmail(resetPasswordVo.getEmail());
        if(user == null){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_NOT_EXIST.getCode(),GlobalCodeConstants.EMAIL_NOT_EXIST.getMsg());
        }
        String code = redisCacheUtils.getCacheObject("forgetPasswordVerifyCode:" + resetPasswordVo.getEmail());
        if(code == null){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_CODE_EXPIRE.getCode(),GlobalCodeConstants.EMAIL_CODE_EXPIRE.getMsg());
        }else if(!code.equals(resetPasswordVo.getCode())){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_ERROR_CODE.getCode(),GlobalCodeConstants.EMAIL_ERROR_CODE.getMsg());
        }
        resetPasswordVo.setPassword(bcryptPasswordEncoder.encode(resetPasswordVo.getPassword()));
        userMapper.resetPassword(resetPasswordVo);
        return AjaxResult.success(1);
    }

    @Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            // 抛UsernameNotFoundException异常
            throw  new UsernameNotFoundException("user " + email + " not exist!");
        }
        // 在这里将用户的权限转换为字符串数组
        List<String> list = new ArrayList<>(Arrays.asList(user.getRole()));
        return new SecurityUser(user,list);
    }
    @Override
    public AjaxResult passwordLogin(LoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response) {
        User user = userMapper.getUserByName(loginUserVo.getName());
        if(user == null){
             return AjaxResult.error(GlobalCodeConstants.USER_NOT_EXIST.getCode(),GlobalCodeConstants.USER_NOT_EXIST.getMsg());
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginUserVo.getName(), loginUserVo.getPassword());
        // 使用authenticationManager调用loadUserByUsername获取数据库中的用户信息,
        try{
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Integer useId = securityUser.getUser().getId();

            List<String> authList = new ArrayList<String>();
            for (GrantedAuthority auth : securityUser.getAuthorities()) {
                authList.add(auth.getAuthority());
            }
            String jwt = JwtUtils.createJwt("user login", useId);
            // 存入Redis
            redisCacheUtils.setCacheObject("login:"+useId,securityUser);
            TokenVo tokenVo = new TokenVo();
            tokenVo.setToken(jwt);
            return AjaxResult.success(tokenVo);
        }catch (Exception e){
            return AjaxResult.error(GlobalCodeConstants.PASSWORD_ERROR.getCode(),GlobalCodeConstants.PASSWORD_ERROR.getMsg());
        }

    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser loginUser = (SecurityUser) authentication.getPrincipal();
        int userid = loginUser.getUser().getId();
        redisCacheUtils.deleteObject("login:"+userid);
    }

    @Override
    public AjaxResult register(RegisterUserVo registerUserVo,HttpServletRequest request) {
        //检查邮箱是否已被注册
        User oldUser = userMapper.getUserByEmail(registerUserVo.getEmail());
        if(oldUser != null){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_REGISTER_REPEAT.getCode(),GlobalCodeConstants.EMAIL_REGISTER_REPEAT.getMsg());
        }
        String code = (String)redisCacheUtils.getCacheObject("registerVerifyCode:" + registerUserVo.getEmail());
        //检查验证码是否一致
        if(!code.equals(registerUserVo.getCode())){
            return AjaxResult.error(GlobalCodeConstants.EMAIL_ERROR_CODE.getCode(),GlobalCodeConstants.EMAIL_ERROR_CODE.getMsg());
        }

        //创建用户, 设置其中的内容
        User user = new User();
        user.setName(registerUserVo.getName());
        user.setEmail(registerUserVo.getEmail());
        // 在这里将用户密码进行加密, 存入数据库 (可不能对密码明文存储)
        user.setPassword(bcryptPasswordEncoder.encode(registerUserVo.getPassword()));
        user.setEnabled(1); //经过管理员确认后才可使用 TODO:这里需要修改
        user.setRole("user");
        userMapper.registerUser(user);
        return AjaxResult.success();
    }
}
