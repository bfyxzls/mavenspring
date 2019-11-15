package com.lind.mavenspring.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequestWrapper装饰器，去动态过滤请求参数和请求头等.
 */
public class DemoHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public DemoHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value.contains("lind")) {
            value = "lind被装饰";
        }
        return value;
    }

}
