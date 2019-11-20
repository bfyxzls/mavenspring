package com.lind.mavenspring.controller;

import com.lind.mavenspring.config.AuthEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;


@RestController
public class TestController {
    static Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Value("${test:none}")
    String applicationBootstrapValue;

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        return request.getParameter("lind") + applicationBootstrapValue;
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

    @GetMapping("/test-call")
    public Callable<String> getCall() {
        logger.info("主线程开始ID：" + Thread.currentThread().getId());
        //创建一个Callable，3秒后返回String类型
        Callable myCallable1 = () -> {
            Thread.sleep(3000);
            System.out.println("call方法执行了");
            logger.info("异步线程ID：" + Thread.currentThread().getId());
            return "call方法返回值";
        };
        logger.info("主线程结束ID：" + Thread.currentThread().getId());
        return myCallable1;
    }

    @AuthEdit("read")
    @GetMapping("/auth")
    public String audit() {
        return "Hello";
    }
}
