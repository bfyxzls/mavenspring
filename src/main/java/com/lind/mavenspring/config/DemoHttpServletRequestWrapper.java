package com.lind.mavenspring.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequestWrapper装饰器，为请求参数 和请求头进行重写.
 */
public class DemoHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public DemoHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        value = value + "lind被装饰";
        return value;
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (name.equals("token")) {
            headerValue = "JWT:" + headerValue;
        }

        return headerValue;
    }
}
