package com.lind.mavenspring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  /**
   * jackson2Json序列化.
   */
  @Test
  public void stringSet() {
    redisTemplate.opsForValue().set("hello", new Contract("zzl", "test"));
  }

  /**
   * set有去重功能.
   */
  @Test
  public void setAdd() {
    redisTemplate.opsForSet().add("helloSet", new Contract("zzl", "test"));
    redisTemplate.opsForSet().add("helloSet", new Contract("zzl", "test"));
  }

  /**
   * list数据可以重复,可以从两头添加数据.
   */
  @Test
  public void listAdd() {
    redisTemplate.opsForList().leftPush("helloList", new Contract("zzl1", "test"));
    redisTemplate.opsForList().leftPush("helloList", new Contract("zzl2", "test"));
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class Contract {
    private String title;
    private String remark;
  }
}
