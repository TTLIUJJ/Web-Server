# FutureTask

Future接口和实现了Future接口的FutureTask类，代表异步计算的结果。

FutureTask实现了Future接口和Runnable接口。因此，FutureTask可以交给Executor执行，也可以直接执行futureTask.run()。

futureTask可以处于三个状态：

- 未启动。futureTask.run()方法还未被执行之前；
- 已启动。futureTask.run()方法正在被执行的过程；
- 已完成：
	- 正常执行结束；
	- 被取消（futureTask.cancle()）；
	- 抛出异常而异常结束。

使用futureTask的get()和cancel()方法，可能出现的状态：

![](http://p5s0bbd0l.bkt.clouddn.com/futureTask.png)

## FutureTask的实现

FutureTask的实现基于AQS，AQS是一个同步框架，它提供通用机制来原子性管理同步状态、阻塞和唤醒线程，以及维护被阻塞线程的队列。基于AQS实现的同步器还有：Semaphore、CountDownLatch和ReentrantReadWriteLock。

每一个基于AQS实现的同步器都会包含两种数据类型的操作：

- 至少一个acquire操作。这个操作阻塞调用线程，除非/直到AQS的状态允许这个线程继续执行。FutureTask的acquire操作为get()/get(long timeout, TimeUnit unit)方法。
- 至少一个release操作。这个操作改变AQS的状态，改变后的状态可允许一个或多个线程被解除阻塞。FutureTask的release操作包括run()方法和cancel(...)方法。




#### FutureTask.run()的执行过程

- 执行在构造函数中执行的任务（Callable.call()） ；
- 以原子方式来更新状态。如果原子操作成功，就设置代表计算结果的变量result的值为Callable.call()的返回值，然后调用AQS.realeaseShared(int arg)；
- 