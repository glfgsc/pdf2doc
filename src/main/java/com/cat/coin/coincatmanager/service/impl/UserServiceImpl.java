package com.cat.coin.coincatmanager.service.impl;

import com.cat.coin.coincatmanager.controller.vo.LoginUserVo;
import com.cat.coin.coincatmanager.controller.vo.RegisterUserVo;
import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.enums.GlobalCodeConstants;
import com.cat.coin.coincatmanager.domain.pojo.Code;
import com.cat.coin.coincatmanager.domain.pojo.SecurityUser;
import com.cat.coin.coincatmanager.domain.pojo.User;
import com.cat.coin.coincatmanager.mapper.UserMapper;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found");
        }
        // 在这里将用户的权限转换为字符串数组
        List<String> list = new ArrayList<>(Arrays.asList(user.getRole()));
        return new SecurityUser(user,list);
    }

    @Override
    public TokenVo login(LoginUserVo loginUserVo, HttpServletRequest request,HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginUserVo.getName(), loginUserVo.getPassword());
        // 使用authenticationManager调用loadUserByUsername获取数据库中的用户信息,
        Authentication authentication = authenticationManager.authenticate(authToken);
        if(authentication == null) {
            throw new RuntimeException("Login false");
        }

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Integer useId = securityUser.getUser().getId();
        String usrName = securityUser.getUsername();

        List<String> authList = new ArrayList<String>();
        for (GrantedAuthority auth : securityUser.getAuthorities()) {
            authList.add(auth.getAuthority());
        }
        String jwt = JwtUtils.createJwt("user login", useId);
        // 存入Redis
        redisCacheUtils.setCacheObject("login:"+useId,securityUser);
        TokenVo tokenVo = new TokenVo();
        tokenVo.setToken(jwt);
        return tokenVo;
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser loginUser = (SecurityUser) authentication.getPrincipal();
        int userid = loginUser.getUser().getId();
        redisCacheUtils.deleteObject("login:"+userid);
    }

    @Override
    public Code register(RegisterUserVo registerUserVo,HttpServletRequest request) {
        //检查邮箱是否已被注册
        User oldUser = userMapper.getUserByEmail(registerUserVo.getEmail());
        if(oldUser != null){
            return GlobalCodeConstants.EMAIL_REGISTER_REPEAT;
        }

        //检查验证码是否一致
        if(!request.getAttribute("code").equals(registerUserVo.getCode())){
            return GlobalCodeConstants.EMAIL_ERROR_CODE;
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
        return GlobalCodeConstants.SUCCESS;
    }
}
