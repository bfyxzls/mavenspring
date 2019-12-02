package com.lind.common.util;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class FilterConfigTest {
  @Test
  public void read() {
    String message = FilterConfigManager.getItemValue("loginServer");
    log.info(message);
  }

  @Test
  public void readFather() {
    String message = FilterConfigManager.getItemValue("url-pattern");
    String[] arr = message.split(",");
    Arrays.stream(arr).forEach(System.out::println);
    log.info(message);
  }

}
