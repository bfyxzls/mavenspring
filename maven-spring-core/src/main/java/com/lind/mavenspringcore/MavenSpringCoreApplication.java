package com.lind.mavenspringcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.activiti.spring.boot.SecurityAutoConfiguration.class
})
public class MavenSpringCoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(MavenSpringCoreApplication.class, args);
  }
}