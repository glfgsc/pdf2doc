package com.cat.coin.coincatmanager.config;

import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.enums.GlobalCodeConstants;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.domain.pojo.SecurityUser;
import com.cat.coin.coincatmanager.mapper.UserMapper;
import com.cat.coin.coincatmanager.utils.JwtUtils;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmailAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private RedisCacheUtils redisCacheUtils;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
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
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(AjaxResult.success(tokenVo));
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
