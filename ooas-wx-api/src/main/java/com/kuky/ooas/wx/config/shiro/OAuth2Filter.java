package com.kuky.ooas.wx.config.shiro;


import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description: OAuth2Filter 判断请求头里面的token是正确，正确则判定用户已经登录
 * @Author Kuky
 * @Date: 2021/6/4 21:03
 * @Version 1.0
 */
@Component
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String token = getRequestToken(req);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    /*
     * @Description:判断是否登录,判断此请求是否需要被shiro处理
        在登录的情况下会走此方法，此方法返回true直接访问控制器
     * @param request
     * @param response
     * @param mappedValue
     * @return: boolean
     * @Author: Kuky
     * @Date: 2021/6/4 22:20
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;//这次请求不会由shiro处理
        }
        return false;
    }


    /*
     * @Description:如果isAccessAllowed方法返回True，则不会再调用onAccessDenied方法，
     *              如果isAccessAllowed方法返回Flase,则会继续调用onAccessDenied方法。
     * 先从请求头里获得令牌字符串，验证其是否过期，是否需要刷新令牌，若需要刷新，则保存至Redis和媒介类ThreadLocalToken
     * @param servletRequest
     * @param servletResponse
     * @return: boolean
     * @Author: Kuky
     * @Date: 2021/6/4 22:17
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        //往响应里面设置响应头
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        //设置跨域，允许跨域请求
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));

        threadLocalToken.clear();

        String token = getRequestToken(req);
        if (StrUtil.isBlank(token)) {
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().print("无效的令牌");
            return false;
        }
        try {
            //验证token有问题的话会抛出异常
            jwtUtil.verifierToken(token);
        } catch (TokenExpiredException e) {
            if (redisTemplate.hasKey(token)) {//需要刷新，客户端令牌过期，Redis令牌没过期
                redisTemplate.delete(token);
                int userId = jwtUtil.getUserId(token);
                token = jwtUtil.createToken(userId);
                //存入新令牌
                redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
                threadLocalToken.setToken(token);
            } else {//客户端和Redis令牌都过期了，则需要用户重新登录
                resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
                resp.getWriter().print("令牌已过期");
                return false;
            }
        } catch (Exception e) {//伪造令牌
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().print("无效的令牌");
            return false;
        }
        //间接调用Realm类, bool=false:认证与授权失败
        boolean bool = executeLogin(servletRequest, servletResponse);
        return bool;
    }

    /*
     * @Description:此方法是Shiro在执行Realm类的认证方法时，判定用户没登录或登录失败，接下来会执行
     * 认证失败会执行
     * @param token
     * @param e
     * @param request
     * @param response
     * @return: boolean
     * @Author: Kuky
     * @Date: 2021/6/4 22:36
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
        try {
            resp.getWriter().print(e.getMessage());
        } catch (Exception exception) {

        }
        return false;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        super.doFilterInternal(request, response, chain);
    }


    private String getRequestToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        return token;
    }
}