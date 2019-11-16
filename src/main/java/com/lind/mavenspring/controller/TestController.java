package com.lind.mavenspring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        return request.getParameter("lind");
    }

    /**
     * 异步建立高并发的订单.
     *
     * @return
     */
    @GetMapping("/create-order")
    public DeferredResult<Object> createOrder() {
        DeferredResult<Object> deferredResult = new DeferredResult<>((long) 3000, "error order");
        OrderQueue.put(deferredResult);
        return deferredResult;
    }

    /**
     * 事实上它是一个订单队列的消费者，在后台写订单，并通知到create-order中.
     *
     * @return
     */
    @GetMapping("/process-order")
    public String processOrder() {
        //建立订单
        String order = UUID.randomUUID().toString();
        DeferredResult<Object> deferredResult = OrderQueue.pop();
        if (deferredResult == null) {
            return "没有要处理的任务";
        }
        //当设置之后，create-order将成功响应
        deferredResult.setResult(order);
        return order;
    }


}
