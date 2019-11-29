package com.lind.mavenspringb.config;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String url=request.getRequestURI();
    if(!url.contains("login")) {
      if (request.getParameter("token") == null) {
        response.sendRedirect("/login");
        return;
      }
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {
    logger.info("AuthFilter.destroy");
  }
}
