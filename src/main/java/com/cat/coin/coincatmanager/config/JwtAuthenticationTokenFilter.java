package com.cat.coin.coincatmanager.config;

import com.cat.coin.coincatmanager.domain.pojo.SecurityUser;
import com.cat.coin.coincatmanager.utils.JwtUtils;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.util.StringUtils;
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("Authorization");
        String[] whiteList = {"/user/email/login",
                "/user/checkUserName",
                "/document/downloadOldFile",
                "/document/downloadNewFile",
                "/user/resetPassword",
                "/user/password/login",
                "/user/register",
                "/user/sendLoginMail",
                "/user/sendRegisterMail",
                "/user/sendForgetPasswordMail",
                "/wechat/checkSignature",
                "/wechat/qrcode"
        };
        if (Arrays.stream(whiteList).anyMatch(e -> e.equals(request.getRequestURI())) || request.getRequestURI().contains("/files/upload") || !StringUtils.hasText(token) || !token.startsWith("Bearer")) {
            //token为空的话, 就不管它, 让SpringSecurity中的其他过滤器处理请求
            //请求放行
            filterChain.doFilter(request, response);
            return;
        }
        //token不为空时, 解析token
        int userid;
        try {
            Claims claims = JwtUtils.parseJWT(token.substring(7));
            //解析出userid
            userid = claims.get("userId", Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //使用userid从Redis缓存中获取用户信息
        String redisKey = "login:" + userid;
        Object loginUser = redisCacheUtils.getCacheObject(redisKey);
        if (!Objects.isNull(loginUser)) {
            //将用户安全信息存入SecurityContextHolder, 在之后SpringSecurity的过滤器就不会拦截
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }
        //放行
        filterChain.doFilter(request, response);
    }
}
