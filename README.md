# Jayna Web Server

**Jayna Web Server基于Reactor多线程模型编写HTTP服务器**

**目前在长连接的情况下，已经表现出不错的性能**

**接触HTTP服务器可以很好的学习网络编程和多线程编程的知识**

**目前的代码量（实时更新）**

![](./readme/picture/代码总量.png)

## Dev Document

| Part Ⅰ | Part Ⅱ | Part Ⅲ | Part Ⅳ | Part Ⅴ | Part Ⅵ | 
| :-----: | :-----: | :-----: | :-----: | :-----: | :-----: | 
| [项目目的](https://github.com/TTLIUJJ/Jayna/blob/master/%E9%A1%B9%E7%9B%AE%E7%9B%AE%E7%9A%84.md)  | [并发模型](https://github.com/TTLIUJJ/Jayna/blob/master/%E5%B9%B6%E5%8F%91%E6%A8%A1%E5%9E%8B.md)  | [架构分析](https://github.com/TTLIUJJ/Jayna/blob/master/%E6%9E%B6%E6%9E%84%E5%88%86%E6%9E%90.md)  | [核心类](https://github.com/TTLIUJJ/Jayna/blob/master/%E6%A0%B8%E5%BF%83%E7%B1%BB.md)  | [遇到的问题](https://github.com/TTLIUJJ/Jayna/blob/master/%E9%81%87%E5%88%B0%E7%9A%84%E9%97%AE%E9%A2%98.md)  | [测试结果](https://github.com/TTLIUJJ/Jayna/blob/master/%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C.md)  | 

---


## Timeline

**Now**

- 实现多线程处理I/O请求
	- ThreadPoolExecutor
	- 工作队列为ArrayBlockingQueue
- 实现定时关闭长连接的Socket
	- ScheduledThreadPoolExecutor
	- 工作队列为DelayQueue，底层实现是PriorityQueue
	- 使用时间轮盘处理大量的长连接
		* 关闭Socket连接的操作，封装为FutureTask，可以重设Socket过期时间
		* 可以唯一识别的SelectionKey与FutrueTask通过ConcurrentHashMap关联（严重拖累CPU，已被废弃）
- 实现了HTTP长连接传输数据
	- 非阻塞I/O
	- I/O复用
	- Selector默认触发模式（LT）
- 实现了状态机解析HTTP协议，非简单字符串匹配请求
	- 支持GET请求
- 实现了Web基础轮子
	- IOC
	- AOP
	- MVC
	
**Feature**

- 支持POST方法请求，扩展状态机
- 实现服务器缓存
- 学习Servlet规范
- 异步错误日志