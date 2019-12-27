package com.lind.mavenspringcore;

import com.lind.mavenspringcore.model.Item;
import com.lind.mavenspringcore.repository.ItemRepository;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(value = "com.lind", lazyInit = true)
public class EsTest {

  @Autowired
  private ElasticsearchTemplate elasticsearchTemplate;
  @Autowired
  private ItemRepository itemRepository;

  /**
   * @Description:创建索引，会根据Item类的@Document注解信息来创建
   * @Author: https://blog.csdn.net/chen_2890
   * @Date: 2018/9/29 0:51
   */
  @Test
  public void testCreateIndex() {
    elasticsearchTemplate.createIndex(Item.class);
  }


  /**
   * @Description:删除索引
   * @Author: https://blog.csdn.net/chen_2890
   * @Date: 2018/9/29 0:50
   */
  @Test
  public void testDeleteIndex() {
    elasticsearchTemplate.deleteIndex(Item.class);
  }

  /**
   * @Description:定义新增方法
   * @Author: https://blog.csdn.net/chen_2890
   */
  @Test
  public void insert() {
    Item item = new Item(1L, "小米手机7", " 手机", "小米", 3499.00, "http://image.baidu.com/13123.jpg");
    itemRepository.save(item);
  }

  @Test
  public void insertList() {
    List<Item> list = new ArrayList<>();
    list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.baidu.com/13123.jpg"));
    list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.baidu.com/13123.jpg"));
    list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.baidu.com/13123.jpg"));
    list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.baidu.com/13123.jpg"));
    list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.baidu.com/13123.jpg"));
    // 接收对象集合，实现批量新增
    itemRepository.saveAll(list);

  }

  @Test
  public void update() {
    Item item = new Item(1L, "苹果XSMax", " 手机", "小米", 3499.00, "http://image.baidu.com/13123.jpg");
    itemRepository.save(item);
  }

  @Test
  public void testQueryAll() {
    // 查找所有
    //Iterable<Item> list = this.itemRepository.findAll();
    // 对某字段排序查找所有 Sort.by("price").descending() 降序
    // Sort.by("price").ascending():升序
    Iterable<Item> list = this.itemRepository.findAll();

    for (Item item : list) {
      System.out.println(item);
    }
  }

  @Test
  public void queryByPriceBetween() {

    List<Item> listResult = this.itemRepository.findByPriceBetween(2000.00, 3500.00);
    for (Item item : listResult) {
      System.out.println("item = " + item);
    }
  }

  /**
   * @Description:matchQuery底层采用的是词条匹配查询
   * @Author: https://blog.csdn.net/chen_2890
   */
  @Test
  public void testMatchQuery() {
    // 构建查询条件
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    // 添加基本分词查询
    queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机7"));
    // 搜索，获取结果
    Page<Item> items = this.itemRepository.search(queryBuilder.build());
    // 总条数
    long total = items.getTotalElements();
    System.out.println("total = " + total);
    for (Item item : items) {
      System.out.println(item);
    }
  }

  /**
   * @Description:分页查询
   * @Author: https://blog.csdn.net/chen_2890
   */
  @Test
  public void searchByPage() {
    // 构建查询条件
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    // 添加基本分词查询
    queryBuilder.withQuery(QueryBuilders.termQuery("title", "小米"));
    // 分页：
    int page = 1;
    int size = 2;
    queryBuilder.withPageable(PageRequest.of(page, size));

    // 搜索，获取结果
    Page<Item> items = this.itemRepository.search(queryBuilder.build());
    // 总条数
    long total = items.getTotalElements();
    System.out.println("总条数 = " + total);
    // 总页数
    System.out.println("总页数 = " + items.getTotalPages());
    // 当前页
    System.out.println("当前页：" + items.getNumber());
    // 每页大小
    System.out.println("每页大小：" + items.getSize());

    for (Item item : items) {
      System.out.println(item);
    }
  }

  /**
   * @Description:排序查询
   * @Author: https://blog.csdn.net/chen_2890
   */
  @Test
  public void searchAndSort() {
    // 构建查询条件
    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
    // 添加基本分词查询
    queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));

    // 排序
    queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));

    // 搜索，获取结果
    Page<Item> items = this.itemRepository.search(queryBuilder.build());
    // 总条数
    long total = items.getTotalElements();
    System.out.println("总条数 = " + total);

    for (Item item : items) {
      System.out.println(item);
    }
  }

}
