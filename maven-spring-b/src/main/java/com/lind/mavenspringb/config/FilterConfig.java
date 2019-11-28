package com.lind.mavenspringb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器注册.
 */
@Configuration
public class FilterConfig {
  /**
   * 注册AuthFilter.
   *
   * @return
   */
  @Bean
  public AuthFilter authFilter() {
    return new AuthFilter();
  }
}
