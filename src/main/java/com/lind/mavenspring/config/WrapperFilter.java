package com.lind.mavenspring.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("WrapperFilter.init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("WrapperFilter.doFilter");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        DemoHttpServletRequestWrapper demoHttpServletRequestWrapper = new DemoHttpServletRequestWrapper((HttpServletRequest) request);
        filterChain.doFilter(demoHttpServletRequestWrapper, response);
    }

    @Override
    public void destroy() {

    }

}