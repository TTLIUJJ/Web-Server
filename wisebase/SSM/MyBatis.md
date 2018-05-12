# MyBatis

目前只会使用注解版。

## 关键字解释

```
    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME,
            " WHERE entity_type = ${entityType} and entity_id = #{entityId} ORDER BY id DESC"})
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType,
                                        @Param("entityId") int entityId);
```

#### 解析语句

首先得知道，注解@Select{...}是让MyBatis执行SQL语句。

entity_type对应的数据库中的字段，而entityType对应的是自定义类中的字段。

```
	WHERE entity_type = ${entityType} and entity_id = #{entityId}
```

上面的SQL语句表示要接受两个参数，参数名分别为entityType和entityId，如果要正确传入参数，那么就就要给参数命名。

```
	returnType foo(@Param("entityType") int entityType, @Param("entityId") int entityId);
```

多参数的输入

- 最佳解决方案是用@Param注解
- 使用封装属性的Model类
- Map

#### ${...}传参和#{...}传参的区别

- 模式#{...}将传入的数据都当前是字符串，会自动将传入的数据加上双引号
如：ORDER BY #{id} 的SQL语句会被解析为 ORDER BY "5"，假设这里id是5

- 模式#{...}很大程度上可以防止SQL注入

- 模式#{...}会导致MyBatis创建预处理语句属性，并以它为背景设置安全的值
比如：

```sql
	SELECT id,name,age FROM student WHERE id = #{id} 
	//会被预编译处理
	SELECT id,name,age FROM student WHERE id = ? 
```

- 一般能用#{...}模式就不用使用${...}模式

- 模式${...}直接将SQL语句暴露出来，无法防止SQL注入

- 模式${...}传入的的数据会被直接生成为SQL，拼接起来。
如：ORDER BY ${id} 的SQL语句会被解析为 ORDER BY 5，假设这里id是5 

- 模式${...}模式一般用于传入数据库对象，比如传入表名，就不能用“table”了

- 在使用动态参数排序时，使用${...}模式

## MyBatis缓存机制

MyBatis默认缓存机制是一级缓存，在使用中容易引起数据脏读。

#### SqlSession

对应着一次数据库会话。由于数据库会话不是永久的，因此SqlSession的生命周期也不是永久的，在每次访问数据库的时候都会创建一个SqlSession（在SqlSession里能执行多次SQL语句，直到关闭SqlSession）。

由于SqlSession不是线程安全的，所以其实例不能共享，每个线程应该有自己的SqlSession实例。比如：收到HTTP请求后，创建一个实例，进行某些操作，之后关闭。

#### 一级缓存 

在应用运行过程中，可能在一次数据库会话中，执行多次查询条件相同的SQL语句，MyBatis提供了一级缓存方案优化这部分的场景，如果是相同的SQL语句，会优先命中一级缓存，避免直接对数据库进行查询，提高性能。

![](https://pic1.zhimg.com/v2-fd8f38852c898d11f372798786dc7f90_b.jpg)

每个Sql Session中持有了Executor，每个Executor中有一个LocalCache。每当用户发起查询时，MyBatis根据当前执行的语句生成MappedStatement，在Local Cache进行查询，如果缓存命中的话，直接返回结果给用户，如果缓存没有命中，再查询数据库，结果写入Local Cache，最后返回给用户。


一级缓存的作用域为Session，当Session Flush或者Close之后，该Seesion中的所有缓存就会被清空。


#### 二级缓存

二级缓存机制指的是在不同的Session之间都可以共享的数据内容。

在上文中提到的一级缓存中，其最大的共享范围就是一个SqlSession内部，如果多个SqlSession之间需要共享缓存，则需要使用到二级缓存。开启二级缓存后，会使用CachingExecutor装饰Executor，进入一级缓存的查询流程前，先在CachingExecutor进行二级缓存的查询，具体的工作流程如下所示。

![](https://pic4.zhimg.com/v2-65b50fa087add440f70e29ce85aa624b_b.jpg)


当开启缓存后，数据的查询执行的流程就是 二级缓存 -> 一级缓存 -> 数据库。


#### 总结

作者：轩辕志远
链接：https://www.zhihu.com/question/41842918/answer/92565345
来源：知乎

一级缓存可以简单理解为会话级或者线程级的缓存，当一个查询发生的时候，Mybatis 会在当前会话中查找是否已经有过相同的查询，有的话就直接拿缓存，不去数据库查了，线程执行完毕，缓存就被清掉了。二级缓存是进程级别的，通过在 mapper 文件中增加  节点来启用，我猜测 Mybatis 的缓存是以 SQL 语句+ 参数 为 key，查询结果为 value 放在Map中的。命中就是一个查询的SQL语句一样并且参数一样，能在缓存 Key 中找到，这个时候就直接返回结果了。Mybatis 的缓存都是放在内存的。

