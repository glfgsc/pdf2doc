package com.cat.coin.coincatmanager.config;

import com.alibaba.fastjson.JSONObject;
import com.cat.coin.coincatmanager.domain.enums.GlobalCodeConstants;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //注入Jwt认证拦截器.
    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(JSONObject.toJSONString(GlobalCodeConstants.UNAUTHORIZED));
        }
    }

    public class CustomAccessDeineHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(JSONObject.toJSONString(GlobalCodeConstants.ACCESS_DENIED));
        }

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() //跨站请求伪造
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //由于我们使用token作为信息传递介质, 所以禁用Session
                .and()
                .authorizeRequests() // 进行认证请求的配置
                .antMatchers("/user/login").anonymous() // 将所有登入和注册的接口放开, 这些都是无需认证就访问的
                .antMatchers("/user/register").anonymous()
                .antMatchers("/email/code").anonymous()
                .antMatchers("/admin/login").anonymous()
                .antMatchers("/admin/register").anonymous()
                .anyRequest().authenticated() //除了上面的那些, 剩下的任何接口请求都需要经过认证
                .and()
                .cors() //允许跨域请求
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeineHandler())
        ;
        //把token校验过滤器添加到过滤器链中, 添加在UsernamePasswordAuthenticationFilter之前是因为只要用户携带token, 就不需要再去验证是否有用户名密码了 (而且我们不使用表单登入, UsernamePasswordAuthenticationFilter是无法解析Json的, 相当于它没用了)
        //UsernamePasswordAuthenticationFilter是SpringSecurity默认配置的表单登录拦截器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // 身份验证管理器, 直接继承即可.
        return super.authenticationManagerBean();
    }

    // 将BCryptPasswordEncoder加密器注入SpringSecurity中, 之后SpringSecurity的DaoAuthenticaionProvider会调用该加密器中的match()方法进行密码比对, 密码比对过程不需要我们干涉
    @Bean
    public BCryptPasswordEncoder bcryptPasswordBean(){
        return new BCryptPasswordEncoder();
    }
}
