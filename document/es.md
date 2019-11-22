Elasticsearch也是基于Lucene的全文检索库，本质也是存储数据，很多概念与MySQL类似的。
# 详细说明
```
概念 	说明
索引库（indices) 	indices是index的复数，代表许多的索引，
类型（type） 	类型是模拟mysql中的table概念，一个索引库下可以有不同类型的索引，比如商品索引，订单索引，其数据格式不同。不过这会导致索引库混乱，因此未来版本中会移除这个概念
文档（document） 	存入索引库原始的数据。比如每一条商品信息，就是一个文档
字段（field） 	文档中的属性
映射配置（mappings） 	字段的数据类型、属性、是否索引、是否存储等特性
```
是不是与Lucene中的概念类似。

另外，在Elasticsearch有一些集群相关的概念：
```
    索引集（Indices，index的复数）：逻辑上的完整索引
    分片（shard）：数据拆分后的各个部分
    副本（replica）：每个分片的复制
```
> 注意：
Elasticsearch本身就是分布式的，因此即便你只有一个节点，Elasticsearch默认也会对你的数据进行分片和副本操作，当你向集群添加新数据时，数据也会在新加入的节点中进行平衡。

# 映射—注解

Spring Data通过注解来声明字段的映射属性，有下面的三个注解：
```
    @Document 作用在类，标记实体类为文档对象，一般有两个属性
        indexName：对应索引库名称
        type：对应在索引库中的类型
        shards：分片数量，默认5
        replicas：副本数量，默认1
    @Id 作用在成员变量，标记一个字段作为id主键
    @Field 作用在成员变量，标记为文档的字段，并指定字段映射属性：
        type：字段类型，是枚举：FieldType，可以是text、long、short、date、integer、object等
            text：存储数据时候，会自动分词，并生成索引
            keyword：存储数据时候，不会分词建立索引
            Numerical：数值类型，分两类
                基本数据类型：long、interger、short、byte、double、float、half_float
                浮点数的高精度类型：scaled_float
                    需要指定一个精度因子，比如10或100。elasticsearch会把真实值乘以这个因子后存储，取出时再还原。
            Date：日期类型
                elasticsearch可以对日期格式化为字符串存储，但是建议我们存储为毫秒值，存储为long，节省空间。
        index：是否索引，布尔类型，默认是true
        store：是否存储，布尔类型，默认是false
        analyzer：分词器名称，这里的ik_max_word即使用ik分词器
```
# 自定义方法名
```
Keyword 	Sample
And 	findByNameAndPrice
Or 	findByNameOrPrice
Is 	findByName
Not 	findByNameNot
Between 	findByPriceBetween
LessThanEqual 	findByPriceLessThan
GreaterThanEqual 	findByPriceGreaterThan
Before 	findByPriceBefore
After 	findByPriceAfter
Like 	findByNameLike
StartingWith 	findByNameStartingWith
EndingWith 	findByNameEndingWith
Contains/Containing 	findByNameContaining
In 	findByNameIn(Collection<String>names)
NotIn 	findByNameNotIn(Collection<String>names)
Near 	findByStoreNear
True 	findByAvailableTrue
False 	findByAvailableFalse
OrderBy 	findByAvailableTrueOrderByNameDesc
```
# 聚合（牛逼！！solr无此功能）

聚合可以让我们极其方便的实现对数据的统计、分析。例如：
* 什么品牌的手机最受欢迎？
* 这些手机的平均价格、最高价格、最低价格？
* 这些手机每月的销售情况如何？

实现这些统计功能的比数据库的sql要方便的多，而且查询速度非常快，可以实现近实时搜索效果。
## 2.5.1 聚合基本概念

Elasticsearch中的聚合，包含多种类型，最常用的两种，一个叫桶，一个叫度量：

    桶（bucket）

桶的作用，是按照某种方式对数据进行分组，每一组数据在ES中称为一个桶，例如我们根据国籍对人划分，可以得到中国桶、英国桶，日本桶……或者我们按照年龄段对人进行划分：010,1020,2030,3040等。

Elasticsearch中提供的划分桶的方式有很多：

* Date Histogram Aggregation：根据日期阶梯分组，例如给定阶梯为周，会自动每周分为一组
* Histogram Aggregation：根据数值阶梯分组，与日期类似
    Terms Aggregation：根据词条内容分组，词条内容完全匹配的为一组
    Range Aggregation：数值和日期的范围分组，指定开始和结束，然后按段分组
  

综上所述，我们发现bucket aggregations 只负责对数据进行分组，并不进行计算，因此往往bucket中往往会嵌套另一种聚合：metrics aggregations即度量

    度量（metrics）

分组完成以后，我们一般会对组中的数据进行聚合运算，例如求平均值、最大、最小、求和等，这些在ES中称为度量

比较常用的一些度量聚合方式：
```
    Avg Aggregation：求平均值
    Max Aggregation：求最大值
    Min Aggregation：求最小值
    Percentiles Aggregation：求百分比
    Stats Aggregation：同时返回avg、max、min、sum、count等
    Sum Aggregation：求和
    Top hits Aggregation：求前几
    Value Count Aggregation：求总数
    ……
```
注意：在ES中，需要进行聚合、排序、过滤的字段其处理方式比较特殊，因此不能被分词。这里我们将color和make这两个文字类型的字段设置为keyword类型，这个类型不会被分词，将来就可以参与聚合
## 2.5.2 聚合为桶

桶就是分组，比如这里我们按照品牌brand进行分组：
```$xslt
/**
     * @Description:按照品牌brand进行分组
     * @Author: https://blog.csdn.net/chen_2890			
     */
	@Test
	public void testAgg(){
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 不查询任何结果
	    queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
	    // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
	    queryBuilder.addAggregation(
	        AggregationBuilders.terms("brands").field("brand"));
	    // 2、查询,需要把结果强转为AggregatedPage类型
	    AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
	    // 3、解析
	    // 3.1、从结果中取出名为brands的那个聚合，
	    // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
	    StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
	    // 3.2、获取桶
	    List<StringTerms.Bucket> buckets = agg.getBuckets();
	    // 3.3、遍历
	    for (StringTerms.Bucket bucket : buckets) {
	        // 3.4、获取桶中的key，即品牌名称
	        System.out.println(bucket.getKeyAsString());
	        // 3.5、获取桶中的文档数量
	        System.out.println(bucket.getDocCount());
	    }
	
	}

```

## 关键API：

 AggregationBuilders：聚合的构建工厂类。所有聚合都由这个类来构建，看看他的静态方法：
 ```
（1）统计某个字段的数量
  ValueCountBuilder vcb=  AggregationBuilders.count("count_uid").field("uid");
（2）去重统计某个字段的数量（有少量误差）
 CardinalityBuilder cb= AggregationBuilders.cardinality("distinct_count_uid").field("uid");
（3）聚合过滤
FilterAggregationBuilder fab= AggregationBuilders.filter("uid_filter").filter(QueryBuilders.queryStringQuery("uid:001"));
（4）按某个字段分组
TermsBuilder tb=  AggregationBuilders.terms("group_name").field("name");
（5）求和
SumBuilder  sumBuilder=	AggregationBuilders.sum("sum_price").field("price");
（6）求平均
AvgBuilder ab= AggregationBuilders.avg("avg_price").field("price");
（7）求最大值
MaxBuilder mb= AggregationBuilders.max("max_price").field("price"); 
（8）求最小值
MinBuilder min=	AggregationBuilders.min("min_price").field("price");
（9）按日期间隔分组
DateHistogramBuilder dhb= AggregationBuilders.dateHistogram("dh").field("date");
（10）获取聚合里面的结果
TopHitsBuilder thb=  AggregationBuilders.topHits("top_result");
（11）嵌套的聚合
NestedBuilder nb= AggregationBuilders.nested("negsted_path").path("quests");
（12）反转嵌套
AggregationBuilders.reverseNested("res_negsted").path("kps ");

```
AggregatedPage：聚合查询的结果类。它是Page<T>的子接口：AggregatedPage在Page功能的基础上，拓展了与聚合相关的功能，它其实就是对聚合结果的一种封装。
## 嵌套聚合，求平均值
```$xslt
@Test
	public void testSubAgg(){
	    NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
	    // 不查询任何结果
	    queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
	    // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
	    queryBuilder.addAggregation(
	        AggregationBuilders.terms("brands").field("brand")
	        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
	    );
	    // 2、查询,需要把结果强转为AggregatedPage类型
	    AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
	    // 3、解析
	    // 3.1、从结果中取出名为brands的那个聚合，
	    // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
	    StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
	    // 3.2、获取桶
	    List<StringTerms.Bucket> buckets = agg.getBuckets();
	    // 3.3、遍历
	    for (StringTerms.Bucket bucket : buckets) {
	        // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
	        System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");
	
	        // 3.6.获取子聚合结果：
	        InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
	        System.out.println("平均售价：" + avg.getValue());
	    }
	
	}
```