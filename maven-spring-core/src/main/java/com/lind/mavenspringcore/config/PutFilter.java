package com.lind.mavenspringcore.config;

import javax.servlet.annotation.WebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.HttpPutFormContentFilter;

@Component
@WebFilter(urlPatterns = "/*")
public class PutFilter extends HttpPutFormContentFilter {

}