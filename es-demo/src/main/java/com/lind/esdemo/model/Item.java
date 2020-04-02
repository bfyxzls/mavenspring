package com.lind.esdemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(indexName = "item", type = "docs", shards = 1, replicas = 0)
public class Item {

  /**
   * @Description: @Id注解必须是springframework包下的.
   * org.springframework.data.annotation.Id.
   * @Author: https://blog.csdn.net/chen_2890
   */
  @Id
  private Long id;

  @Field(type = FieldType.Text, analyzer = "ik_max_word")
  private String title;

  @Field(type = FieldType.Keyword)
  private String category;

  @Field(type = FieldType.Keyword)
  private String brand;

  @Field(type = FieldType.Double)
  private Double price;

  @Field(index = false, type = FieldType.Keyword)
  private String images;
}

