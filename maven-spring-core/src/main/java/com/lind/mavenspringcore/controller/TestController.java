package com.lind.mavenspringcore.controller;

import com.lind.mavenspringcore.config.AuthEdit;
import com.lind.mavenspringcore.feign.MavenSpringBClient;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
public class TestController {
  static Logger logger = LoggerFactory.getLogger(TestController.class);
  @Autowired
  ApplicationEventPublisher applicationEventPublisher;

  @Value("${svt:none}")
  String applicationBootstrapValue;
  @Autowired
  MavenSpringBClient mavenSpringBClient;

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

  @GetMapping("/service-b")
  public String serviceB() {
    return mavenSpringBClient.getInfo();
  }

  /**
   * 测试put方法收到的参数值.
   *
   * @param modelId 路由参数
   * @param name    x-www-form-urlencoded键值对
   */
  @RequestMapping(value = "/test/{modelId}/save", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> saveModel(@PathVariable String modelId,
                                          @RequestParam String name) {
    return new ResponseEntity<>("Body param map: " + name, HttpStatus.OK);
  }

  @RequestMapping(value = "/test/{modelId}/update", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<String> updateModel(@PathVariable String modelId,
                                            @RequestParam String name) {
    return new ResponseEntity<>("Body param map: " + name, HttpStatus.OK);
  }
}
