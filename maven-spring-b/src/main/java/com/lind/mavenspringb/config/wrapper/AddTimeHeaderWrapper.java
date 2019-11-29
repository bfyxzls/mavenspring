package com.lind.mavenspringb.config.wrapper;

import cn.hutool.core.date.DateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 时间装饰器,为指定参数添加时间后缀.
 */
public class AddTimeHeaderWrapper extends HttpServletRequestWrapper {
  public AddTimeHeaderWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getParameter(String name) {
    String value = super.getParameter(name);
    if (name.equals("token")) {
      value = value + "_" + DateTime.now().toString();
    }
    return value;
  }

}
