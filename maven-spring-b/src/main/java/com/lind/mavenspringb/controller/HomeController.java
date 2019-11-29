package com.lind.mavenspringb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  @Value("${message}")
  String message;

  @GetMapping("/hello")
  public String getInfo() {
    return "service b data" + message;
  }

  @GetMapping("/login")
  public String login() {
    return "需要先去登录";
  }
}
