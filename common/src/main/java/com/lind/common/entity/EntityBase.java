package com.lind.common.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class EntityBase {
  private String id;
  private Date createTime;
  private String createBy;
}
