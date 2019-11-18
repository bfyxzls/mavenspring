package com.lind.mavenspring.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器的使用
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("access_token");
        //没有access_token将是一个未授权的访问
        if (StringUtils.isEmpty(accessToken) || StringUtils.isBlank(accessToken)) {
            return false;
        }
        return true;
    }
}
