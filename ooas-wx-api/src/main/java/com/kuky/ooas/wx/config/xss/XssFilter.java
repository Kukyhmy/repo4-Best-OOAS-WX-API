package com.kuky.ooas.wx.config.xss;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Description: XssFilter
 * @Author Kuky
 * @Date: 2021/6/4 17:30
 * @Version 1.0
 */
@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
        filterChain.doFilter(wrapper, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}