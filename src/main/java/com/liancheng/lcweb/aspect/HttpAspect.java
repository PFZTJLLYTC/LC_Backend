package com.liancheng.lcweb.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HttpAspect {

    //日志记录
    private final static Logger log = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("execution(public * com.liancheng.lcweb.controller.*.*(..))")
    public void log(){ }

    @Before("log()")
    public void dobefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //message
        log.info("--------doBefore--------");

        //url
        log.info("url={}",request.getRequestURL());

        //method
        log.info("method={}",request.getMethod());

        //ip
        log.info("id={}",request.getRemoteAddr());

        //类方法　传入对象joinpoint
        log.info("class_method={}",joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

        //参数
        //todo 之后删掉，维持保密状态
        log.info("args={}",joinPoint.getArgs());

    }

    @After("log()")
    public void doAfter() {
        log.info("--------doAfter--------");
    }

    @AfterReturning(returning = "object",pointcut = "log()")
    public void doAfterReturning(Object object){
        log.info("response={}",object);
    }

}
