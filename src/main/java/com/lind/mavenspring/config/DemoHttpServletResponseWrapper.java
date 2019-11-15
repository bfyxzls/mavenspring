package com.lind.mavenspring.config;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * 装饰输出流.
 */
public class DemoHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer = null;
    private ServletOutputStream out = null;
    private PrintWriter writer = null;

    public DemoHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}
