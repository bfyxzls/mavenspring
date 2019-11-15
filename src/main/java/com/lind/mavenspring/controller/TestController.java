package com.lind.mavenspring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        return request.getParameter("lind");
    }
}
