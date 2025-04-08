package com.cat.coin.coincatmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义手机登录认证过滤器
 */
public class EmailAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_EMAIL_KEY = "email";

    public static final String SPRING_SECURITY_CODE_KEY = "code";

    private String emailParameter = SPRING_SECURITY_EMAIL_KEY;

    private String codeParameter = SPRING_SECURITY_CODE_KEY;

    private boolean postOnly = true;

    public EmailAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    public EmailAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        if (postOnly && !httpServletRequest.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + httpServletRequest.getMethod());
        }
        Map<String, String> loginData = new HashMap<>(2);
        try {
            loginData = new ObjectMapper().readValue(httpServletRequest.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException("请求参数异常");
        }

        // 获得请求参数
        String email = loginData.get(emailParameter);
        String code = loginData.get(codeParameter);

        if (email==null) {
            email = "";
        } else {
            email = email.trim();
        }
        if (code==null) {
            code = "";
        } else {
            code = code.trim();
        }
        EmailAuthenticationToken authRequest = new EmailAuthenticationToken(new ArrayList<>(), email, code);
        this.setDetails(httpServletRequest, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected String obtainEmail(HttpServletRequest request) throws IOException {
        ServletInputStream in = request.getInputStream();
        //将数据流中的数据反序列化成Map
        HashMap<String,String> loginInfo = new ObjectMapper().readValue(in, HashMap.class);
        return loginInfo.get(emailParameter);
    }

    protected String obtainCode(HttpServletRequest request) throws IOException {
        ServletInputStream in = request.getInputStream();
        //将数据流中的数据反序列化成Map
        HashMap<String,String> loginInfo = new ObjectMapper().readValue(in, HashMap.class);
        return loginInfo.get(codeParameter);
    }

    protected void setDetails(HttpServletRequest request, EmailAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
