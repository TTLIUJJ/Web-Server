# Jayna HTTP Server

**Jayna Web Server基于Reactor多线程模型编写HTTP服务器**

**目前在长连接的情况下，已经表现出不错的性能**

**接触HTTP服务器可以很好的学习网络编程和多线程编程的知识**

**目前的代码量（实时更新）**

![](http://owj98yrme.bkt.clouddn.com/cloc3.jpg)

## Dev Document

| Part Ⅰ | Part Ⅱ | Part Ⅲ | Part Ⅳ | Part Ⅴ | Part Ⅵ | 
| :-----: | :-----: | :-----: | :-----: | :-----: | :-----: | 
| [项目目的]() | [并发模型]() | [架构分析]() | [核心类]() | [遇到的问题]() | [测试结果]() | 


---


## Timeline

**Now**

- 实现多线程处理I/O请求
	- ThreadPoolExecutor
	- 工作队列为ArrayBlockingQueue
- 实现定时关闭长连接的Socket
	- ScheduledThreadPoolExecutor
	- 工作队列为DelayQueue，底层实现是PriorityQueue
	- 关闭Socket连接的操作，封装为FutureTask，可以重设Socket过期时间
	- 可以唯一识别的SelectionKey与FutrueTask通过ConcurrentHashMap关联
- 实现了HTTP长连接传输数据
	- 非阻塞I/O
	- I/O复用
	- Selector默认触发模式（LT）
- 实现了状态机解析HTTP协议，非简单字符串匹配请求
	- 支持GET请求
	
**Feature**

- 支持POST方法请求，扩展状态机
- 实现服务器缓存
- 学习Servlet规范