package com.lind.mavenspringb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  @GetMapping("/getinfo")
  public String getInfo() {
    return "service b data";
  }
}
