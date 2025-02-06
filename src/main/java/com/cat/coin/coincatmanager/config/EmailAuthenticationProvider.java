package com.cat.coin.coincatmanager.config;

import com.cat.coin.coincatmanager.controller.vo.TokenVo;
import com.cat.coin.coincatmanager.domain.pojo.SecurityUser;
import com.cat.coin.coincatmanager.service.UserService;
import com.cat.coin.coincatmanager.utils.JwtUtils;
import com.cat.coin.coincatmanager.utils.RedisCacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class EmailAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisCacheUtils redisCacheUtils;

    private UserService userService;

    private RedisTemplate redisTemplate;

    private boolean forcePrincipalAsString = false;

    private static final Logger logger = LoggerFactory.getLogger(EmailAuthenticationProvider.class);

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    public EmailAuthenticationProvider(UserService userService, RedisTemplate redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 认证方法
     * @param authentication 认证token
     * @return successAuthenticationToken
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 首先判断authentication参数必须是一个MobilePhoneAuthenticationToken类型对象
        Assert.isInstanceOf(EmailAuthenticationToken.class, authentication,
                ()-> this.messages.getMessage("MobilePhoneAuthenticationProvider.onlySupports", "Only MobilePhoneAuthenticationToken is supported"));
        // 获取authentication参数的principal属性作为邮箱
        String email = authentication.getPrincipal().toString();
        if (StringUtils.isEmpty(email)) {
            logger.error("email cannot be null");
            throw new BadCredentialsException("email cannot be null");
        }
        // 获取authentication参数的credentials属性作为短信验证码
        String code = authentication.getCredentials().toString();
        if (StringUtils.isEmpty(code)) {
            logger.error("code cannot be null");
            throw new BadCredentialsException("code cannot be null");
        }
        try {
            // 调用userService服务根据手机号查询用户信息
            SecurityUser user = (SecurityUser) userService.loadUserByEmail(email);
            // 校验用户账号是否过期、是否被锁住、是否有效等属性
            userDetailsChecker.check(user);
            // 根据手机号组成的key值去redis缓存中查询发送短信验证码时存储的验证码
            String storedCode = (String) redisTemplate.opsForValue().get("loginVerifyCode:"+email);
            if (storedCode==null) {
                logger.error("code is expired");
                throw new BadCredentialsException("code is expired");
            }
            // 用户登录携带的短信验证码与redis中根据手机号查询出来的登录认证短信验证码不一致则抛出验证码错误异常
            if (!code.equals(storedCode)) {
                logger.error("the code is not correct");
                throw new BadCredentialsException("the code is not correct");
            }
            // 把完成的用户信息赋值给组成返回认证token中的principal属性值
            Object principalToReturn = user;
            // 如果强制把用户信息转成字符串，则只返回用户的手机号码
            if(isForcePrincipalAsString()) {
                principalToReturn = user.getUser().getEmail();
            }
            // 认证成功则返回一个MobilePhoneAuthenticationToken实例对象，principal属性为较为完整的用户信息
            EmailAuthenticationToken successAuthenticationToken = new EmailAuthenticationToken(user.getAuthorities(), principalToReturn, code);
            return successAuthenticationToken;
        } catch (UsernameNotFoundException e) {
            // 用户手机号不存在，如果用户已注册提示用户先去个人信息页面添加手机号码信息，否则提示用户使用手机号注册成为用户后再登录
            logger.error("user " + email + "not found, if you have been register as a user, please goto the page of edit user information to  add you phone number, " +
                    "else you must register as a user use you phone number");
            throw new BadCredentialsException("user " +email + "not found, if you have been register as a user, please goto the page of edit user information to  add you phone number, " +
                    "else you must register as a user use you phone number");
        } catch (NumberFormatException e) {
            logger.error("invalid phoneNo, due it is not a number");
            throw new BadCredentialsException("invalid phoneNo, due do phoneNo is not a number");
        }
    }

    /**
     * 只支持自定义的MobilePhoneAuthenticationToken类的认证
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EmailAuthenticationToken.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.messages, "A message source must be set");
        Assert.notNull(this.redisTemplate, "A RedisTemplate must be set");
        Assert.notNull(this.userService, "A UserDetailsService must be set");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
        this.forcePrincipalAsString = forcePrincipalAsString;
    }

    public boolean isForcePrincipalAsString() {
        return forcePrincipalAsString;
    }
}
