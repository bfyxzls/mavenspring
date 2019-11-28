package com.lind.mavenspringb.config;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 过滤器.
 */
public class AuthFilter implements Filter {

  static Logger logger = LoggerFactory.getLogger(AuthFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    logger.info("AuthFilter.init");
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    logger.info("AuthFilter.doFilter");
  }

  @Override
  public void destroy() {
    logger.info("AuthFilter.destroy");
  }
}
