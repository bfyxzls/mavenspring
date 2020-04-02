package com.lind.esdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
public class EsDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(EsDemoApplication.class, args);
  }
}
