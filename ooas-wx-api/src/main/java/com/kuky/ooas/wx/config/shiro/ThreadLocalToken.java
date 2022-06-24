package com.kuky.ooas.wx.config.shiro;

/**
 * @Description: ThreadLocalToken
 * @Author Kuky
 * @Date: 2021/6/4 20:34
 * @Version 1.0
 */

import org.springframework.stereotype.Component;

/**
 * 在OAuth2Filter类和TokenAspect类之间传递token，因为这两个类之间没有调用关系，ThreadLocalToken起到存储令牌的第三方媒介作用，
 */
@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal<>();

    public void setToken(String token) {
        local.set(token);
    }

    public String getToken() {
        return local.get();
    }

    public void clear() {
        local.remove();
    }
}