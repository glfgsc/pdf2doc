package com.cat.coin.coincatmanager.config;

import com.cat.coin.coincatmanager.domain.enums.GlobalCodeConstants;
import com.cat.coin.coincatmanager.domain.pojo.AjaxResult;
import com.cat.coin.coincatmanager.service.UserService;
import com.cat.coin.coincatmanager.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
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

    @Autowired
    private UserService userService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            System.out.println(request.getRequestURI());
            //todo your business
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            String resBody = objectMapper.writeValueAsString(GlobalCodeConstants.UNAUTHORIZED);
            PrintWriter printWriter = response.getWriter();
            printWriter.print(resBody);
            printWriter.flush();
            printWriter.close();
        }
    }

    public class SimpleAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            //todo your business
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            String resBody = objectMapper.writeValueAsString(GlobalCodeConstants.ACCESS_DENIED);
            PrintWriter printWriter = response.getWriter();
            printWriter.print(resBody);
            printWriter.flush();
            printWriter.close();
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
        EmailAuthenticationProvider emailAuthenticationProvider = emailAuthenticationProvider();
        auth.authenticationProvider(emailAuthenticationProvider);
    }


    @Bean
    public EmailAuthenticationProvider emailAuthenticationProvider() {
        EmailAuthenticationProvider emailAuthenticationProvider = new EmailAuthenticationProvider(userService, redisTemplate);
        return emailAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable() //跨站请求伪造
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //由于我们使用token作为信息传递介质, 所以禁用Session
                .and()
                .authorizeRequests() // 进行认证请求的配置
                .antMatchers("/wechat/**").permitAll()
                .antMatchers("/user/email/login").permitAll() // 将所有登入和注册的接口放开, 这些都是无需认证就访问的
                .antMatchers("/user/password/login").permitAll() // 将所有登入和注册的接口放开, 这些都是无需认证就访问的
                .antMatchers("/user/register").permitAll()
                .antMatchers("/user/resetPassword").permitAll()
                .antMatchers("/user/sendLoginMail").permitAll()
                .antMatchers("/user/sendRegisterMail").permitAll()
                .antMatchers("/user/sendForgetPasswordMail").permitAll()
                .antMatchers("/document/downloadOldFile").permitAll()
                .antMatchers("/document/downloadNewFile").permitAll()
                .antMatchers("/user/checkUserName").permitAll()
                .antMatchers("/admin/login").permitAll()
                .antMatchers("/admin/register").permitAll()
                .antMatchers("/files/upload/**").permitAll()
                .anyRequest().authenticated() //除了上面的那些, 剩下的任何接口请求都需要经过认证
                .and()
                .cors() //允许跨域请求
        ;

        //把token校验过滤器添加到过滤器链中, 添加在UsernamePasswordAuthenticationFilter之前是因为只要用户携带token, 就不需要再去验证是否有用户名密码了 (而且我们不使用表单登入, UsernamePasswordAuthenticationFilter是无法解析Json的, 相当于它没用了)
        //UsernamePasswordAuthenticationFilter是SpringSecurity默认配置的表单登录拦截器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加手机登录认证过滤器,在构造函数中设置拦截认证请求路径
        EmailAuthenticationFilter emailAuthenticationFilter = new EmailAuthenticationFilter("/user/email/login");
        // 下面这个authenticationManager必须设置，否则在MobilePhoneAuthenticationFilter#attemptAuthentication
        // 方法中调用this.getAuthenticationManager().authenticate(authRequest)方法时会报NullPointException
        emailAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        emailAuthenticationFilter.setAllowSessionCreation(true);
        emailAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        emailAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        http.addFilterBefore(emailAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().accessDeniedHandler(new SimpleAccessDeniedHandler()).authenticationEntryPoint(new SimpleAuthenticationEntryPoint());
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 将BCryptPasswordEncoder加密器注入SpringSecurity中, 之后SpringSecurity的DaoAuthenticaionProvider会调用该加密器中的match()方法进行密码比对, 密码比对过程不需要我们干涉
    @Bean
    public BCryptPasswordEncoder bcryptPasswordBean(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        // 自定义的成功后的处理器
        return new EmailAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        // 自定义的成功后的处理器
        return new EmailAuthenticationFailureHandler();
    }
}
