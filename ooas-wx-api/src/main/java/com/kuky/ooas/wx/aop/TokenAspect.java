package com.kuky.ooas.wx.aop;

import com.kuky.ooas.wx.common.util.R;
import com.kuky.ooas.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: TokenAspect 利用切面类向客户端返回令牌
 * @Author Kuky
 * @Date: 2021/6/4 23:19
 * @Version 1.0
 */
@Aspect
@Component
public class TokenAspect {

    @Autowired
    private ThreadLocalToken threadLocalToken;

    /*
     * @Description:拦截所有的Web方法返回值
     * @param
     * @Author: Kuky
     * @Date: 2021/6/4 23:23
     */
    @Pointcut("execution(public * com.example.emos.wx.controller.*.*(..))")
    public void aspect() {

    }

    /*
     * @Description:判断是否刷新生成新令牌：1.检查ThreadLocal中是否保存令牌  2.把新令牌绑定到R对象中
     * @param point
     * @return: java.lang.Object
     * @Author: Kuky
     * @Date: 2021/6/4 23:24
     */
    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        R r = (R) point.proceed();//方法执行结果
        String token = threadLocalToken.getToken();
        //如果ThreadLocal中存在Token，说明是更新的Token
        if (token != null) {
            r.put("token", token);//往响应中放置Token
            threadLocalToken.clear();
        }
        return r;
    }
}