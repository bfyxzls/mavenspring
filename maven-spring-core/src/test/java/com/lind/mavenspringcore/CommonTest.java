package com.lind.mavenspringcore;

import cn.hutool.core.date.DateTime;
import org.junit.Test;

public class CommonTest {
  @Test
  public void utilTest() {
    System.out.println(com.lind.common.util.DateUtils.formatDateTime(DateTime.now()));
  }
}
