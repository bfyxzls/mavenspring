package com.lind.mavenspringb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  @Value("${message}")
  String message;

  @GetMapping("/hello")
  public String getInfo(@RequestParam(required = false) String token) {
    return "service b data" + message + ",token:" + token;
  }

  @GetMapping("/login")
  public String login() {
    return "欢迎您来到登陆页面";
  }
}
