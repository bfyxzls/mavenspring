package com.lind.activiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.activiti.spring.boot.SecurityAutoConfiguration.class
})
public class ActivitiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ActivitiApplication.class, args);
  }
}
