package com.kuky.ooas.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Description: OAuth2Token
 * @Author Kuky
 * @Date: 2021/6/4 18:19
 * @Version 1.0
 */
public class OAuth2Token implements AuthenticationToken {
    private String token;


    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}