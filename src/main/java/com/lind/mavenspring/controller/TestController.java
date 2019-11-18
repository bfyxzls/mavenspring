package com.lind.mavenspring.controller;

import com.lind.mavenspring.config.AuthEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;


@RestController
public class TestController {
    static Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        return request.getParameter("lind");
    }

    @GetMapping("/header")
    public String helloHeader(HttpServletRequest request) {
        return request.getHeader("token");
    }

    /**
     * 异步建立高并发的订单.
     *
     * @return
     */
    @GetMapping("/create-order")
    public DeferredResult<Object> createOrder() {
        DeferredResult<Object> deferredResult = new DeferredResult<>((long) 3000, "error order");
        logger.info("发布建立订单的事件,线程ID：" + Thread.currentThread().getId());
        applicationEventPublisher.publishEvent(deferredResult);
        logger.info("订单的事件完成,线程ID：" + Thread.currentThread().getId());
        return deferredResult;
    }

    @AuthEdit("read")
    @GetMapping("/auth")
    public String audit() {
        return "Hello";
    }
}
