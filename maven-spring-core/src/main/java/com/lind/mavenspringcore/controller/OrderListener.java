package com.lind.mavenspringcore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@Component
@EnableAsync
public class OrderListener {

    static Logger logger = LoggerFactory.getLogger(OrderListener.class);

    /**
     * 事实上它是一个订单队列的消费者，在后台写订单，本例使用简单的事件监听器实现异步处理的功能.
     *
     * @return
     */
    @EventListener
    public String processOrder(DeferredResult<Object> deferredResult) throws InterruptedException {
        logger.info("处理订单并返回到对应的Http上下文");
        String order = UUID.randomUUID().toString();
        Thread.sleep(2000);//假设处理数据需要5秒，前端需要阻塞5秒，但http主线程已经释放了，比较适合IO密集型场合
        //当设置之后，create-order将成功响应
        deferredResult.setResult(order);
        return order;
    }
}