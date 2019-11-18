package com.lind.mavenspring.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 为Wrapper提供的过滤器.
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/hello/*")
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
        DemoHttpServletRequestWrapper demoHttpServletRequestWrapper = new DemoHttpServletRequestWrapper(req);
        DemoHttpServletResponseWrapper demoHttpServletResponseWrapper = new DemoHttpServletResponseWrapper(resp);
        filterChain.doFilter(demoHttpServletRequestWrapper, demoHttpServletResponseWrapper);
    }

    @Override
    public void destroy() {

    }

}
