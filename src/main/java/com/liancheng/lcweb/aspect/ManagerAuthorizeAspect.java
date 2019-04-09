package com.liancheng.lcweb.aspect;


import com.liancheng.lcweb.constant.CookieConstant;
import com.liancheng.lcweb.constant.RedisConstant;
import com.liancheng.lcweb.exception.ManagerAuthorizeException;
import com.liancheng.lcweb.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Component
public class ManagerAuthorizeAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    //日志也可以用这个来搞，暂时未弄

    @Pointcut("execution(public * com.liancheng.lcweb.controller.ManagerController.*(..))"+
            "&& !execution(public * com.liancheng.lcweb.controller.ManagerController.log*(..))")
    public void verify(){}


    @Before("verify()")
    public void doVerify(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //查cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null){
            log.warn("cookie中查不到此用户信息,无法配合其操作");
            throw new ManagerAuthorizeException();
        }

        //如有cookie再查是否匹配
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()))+"";
        if (StringUtils.isEmpty(tokenValue)){
            log.warn("redis中无此用户信息，无法配合其操作");
            throw new ManagerAuthorizeException();
        }

    }
}
