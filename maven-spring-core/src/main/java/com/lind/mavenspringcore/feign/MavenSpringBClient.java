package com.lind.mavenspringcore.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("maven-spring-b")
public interface MavenSpringBClient {
  @GetMapping("/getinfo")
  String getInfo();
}
