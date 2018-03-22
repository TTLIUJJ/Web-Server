# 线程和锁

## 线程的状态

Java线程在运行的生命周期可能处于6种不同的状态。

| 状态名称 |  说明 |
| - | - |
|NEW|初始状态，线程被构建，但是还没有调用start()方法|
|RUNNABLE|运行状态，Java线程将操作系统的就绪和运行两种状态笼统的称为“运行中”|
|BLOCKING|阻塞状态，表示线程阻塞于锁|
|WAITING|等待状态，表示线程进入等待状态，进入该状态表示当前线程需要等待其他线程做出一些特定动作（通知或中断）|
|TIME_WAITING|超时等待状态，可以在执行时间内返回|
|TERMINATED|终止状态，表示当前线程已经执行完毕|

![](http://p5s0bbd0l.bkt.clouddn.com/thread1.jpg)

## 创建线程的三种方式

```
public class CreateThread {
    public static void main(String []args) throws Exception{
        //方法一：实现Runnable,
        //       创建线程
        Runnable_1 r1 = new Runnable_1();
        Thread thread1 = new Thread(r1);
        thread1.start();

        //方法二：继承线程并直接创建
        Thread_2 thread2 = new Thread_2();
        thread2.start();

        //方法三： 实现Callable<T>接口
        //        实现FutureTask类
        //        调用FutureTask.run() 或者 创建线程
        Callable_3 c3 = new Callable_3();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(c3);
//        futureTask.run();
        Thread thread3 = new Thread(futureTask);
        thread3.start();
        int i = futureTask.get();
        System.out.println("get: " + i);
    }
}


class Runnable_1 implements Runnable{
    public void run(){ System.out.println("Thread 1"); }
}

 class Thread_2 extends Thread{
    @Override
    public void run(){ System.out.println("Thread 2"); }
}

class Callable_3 implements Callable<Integer> {
    public Integer call(){
        System.out.println("Thread 3");
        return 1;
    }
}
```

## Daemon线程

当一个Java虚拟机不存在非Daemon线程的时候，Java虚拟机会退出。可以调用Thread.setDaemon(true)将线程设置为守护Daemon线程。

## start()方法和run()方法

start()方法的含义是：当前线程，即主线程告知Java虚拟机，只要线程规划器空闲，应立即启动调用start()方法的线程。
调用线程的start()方法之后，线程处于就绪状态，并没有运行。

- 在主线程内调用某一线程A的start()方法，那么主线程和线程A竞争CPU资源；
- 在主线程内调用某一线程A的run()方法，串行执行代码。

## join()方法

```
// 主线程
public class Parent extends Thread {
    public void run() {
        Child child = new Child();
        child .start();
        child .join();
        // ...
    }
}

// 子线程
public class Child extends Thread {
    public void run() {
        // ...
    }
}
```

上面代码的意思：Parent线程等待Child线程执行完毕才继续执行下面的代码。

## yield()方法

当前线程让出CPU资源，加入CPU资源的竞争状态

## Daemon线程

首先要调用Thread.start()，再调用Thread.setDaemon(true)将一个线程设为守护线程，顺序不能乱。如果一个程序中没有了非守护线程，Java虚拟机中的所有Daemon线程都需要立即终止。

在构建Daemon线程时，不能依靠finnally块中的内容来确保执行关闭或清理资源的逻辑。

## 中断

中断可以理解为线程的一个标识位属性，它表示一个运行中的线程是否被其他线程进行了中断操作。其他线程通过调用被中断线程的interrupt()方法对其进行中断操作。

线程通过检查自身是否被中断来进行响应，线程通过方法isInterrupted()来进行判断是否被中断，也可以调用静态方法Thread.isInterruptted对当前线程的中断进行false复位。

如果线程已经处于终止状态，即使该线程被中断过，在调用该线程的isInterrupted依旧会返回false。

许多声明抛出InterruptedException()的方法（例如TimeUnit.SECONDS.sleep(long time)方法），在抛出InterruptedException之前，Java虚拟机会将该线程的中断标识位复位为false，再抛出IE异常，如果之后调用线程isInterrupted()方法依旧会返回false。

```
public class NotReally {

    public static void main (String []args) throws Exceptio
        IRunner iRunner = new IRunner();
        Thread thread = new Thread(iRunner);
        thread.start();

        TimeUnit.SECONDS.sleep(1);
        thread.interrupt();
        System.out.println("isInterrupted ? " + thread.isInterrupted());
    }

class IRunner implements Runnable{
    public void run(){
        while (true){
            try {
                Thread.sleep(10);
            }catch (Exception e){
                //...
            }
        }
    }
}
```

上面代码输出结果是true，并不像书中说的那样！！！

#### 优雅地终止线程

使用一个boolean变量或者中断操作来终止线程，使线程有机会在终止时去清理资源，而不是武断地将线程终止，这种做法显得更优雅和安全。

```
public class ShutdownGraceful {

    public static void main (String []args) throws Exception{
        Runner one = new Runner();
        Runner two = new Runner();
        Thread thread1 = new Thread(one, "CountThreadOne");
        Thread thread2 = new Thread(two, "CountThreadTwo");

        thread1.start();
        thread2.start();

        TimeUnit.SECONDS.sleep(1);
        thread1.interrupt();    //终止方法1

        TimeUnit.SECONDS.sleep(1);
        two.cancel();           //终止方法2
    }

    private static class Runner implements Runnable{
        private long i;
        private volatile boolean on = true;
        public void run(){
            while(on && !Thread.currentThread().isInterrupted()){
                ++i;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel(){
            on = false;
        }
    }
}

```

## 原子操作

在Java中可以使用锁和循环CAS的方式来实现原子操作。

- 使用总线锁保证原子性，使用处理器提供一个LOCK#信号，当一个处理器在总线上输出此信号，其他处理器的请求将被阻塞，该处理器可以独占内存；
- 使用缓存锁保证原子性，一个处理器的缓存回写到内存会导致其他处理器的缓存无效。

## 使用CAS实现原子操作的问题

#### ABA问题

首先检查当前引用是否等于预期引用，并且检查当前标志是否等于预期标志。

```
public boolean compareAndSet(
	V expectedReference,	//预期引用
	V newReference,		//更新后的引用
	int expectedStamp,	//预期标志
	int newStamp		//更新后的标志
)
``` 

#### 循环时间长开销大

#### 只能保证一个共享变量的原子操作

使用AtomicReference类来保证引用对象之间的原子性，就可以把多个变量放在一个对象里进行CAS操作。

## volatile关键字

- 在单例中的使用；
- 可见性，读一个votile变量，总能看到（任意线程）对这个volatile变量最后的写入；
- 原子性，对任意单个volatile变量的读/写具有原子性，但类似于volatile++这种复合操作不具有原子性。

## Synchronized关键字

Synchronized可以修饰方法或者以同步块形式来进行使用，它主要确保多个线程在同一个是时刻，只能有一个线程处于方法或者同步块中，它保证了线程对变量访问的可见性和排他性。


那么，Synchronized到底锁住什么？

```
public class SynchronizedSample {

    //单例
    public static final SynchronizedSample fixedObject = new SynchronizedSample();

    //非静态同步方法
    public synchronized void ordinaryFunction() throws Exception{
        TimeUnit.SECONDS.sleep(5);
    }
    
    //非静态非同步方法
    public  void ordinaryFunction(int i) {
        System.out.println("ordinaryFunction- arg: int");
    }

    //静态同步方法
    public static synchronized void staticFunction() throws Exception{
        TimeUnit.SECONDS.sleep(5);
    }

    public SynchronizedSample(){}
    public SynchronizedSample(int i){
        System.out.println("Test4 new Object");
    }

    public static void main(String []args) throws Exception{
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                11,
                11,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10));
        pool.prestartAllCoreThreads();

//        pool.execute(new Test1());
//        pool.execute(new Test3(fixedObject));
        for(int i = 0; i < 10; ++i){
//            pool.execute(new Thread(new Test1()));
//            pool.execute(new Thread(new Test2()));
//            pool.execute(new Thread(new Test3(fixedObject)));
//            pool.execute(new Thread(new Test3(new SynchronizedSample())));
//            pool.execute(new Test4());
//            pool.execute(new Test5(fixedObject));
        }

        pool.shutdown();
    }
}


class Test1 implements Runnable{

    public void run(){
        try{
            SynchronizedSample.staticFunction();
            System.out.println("Test1 hello world");
        }catch (Exception e) {

        }
    }
}

class Test2 implements Runnable{
    public void run(){
        try{
            synchronized (SynchronizedSample.class){
                TimeUnit.SECONDS.sleep(5);
            }
            System.out.println("Test2 hello world");
        }catch (Exception e){

        }
    }
}

class Test3 implements Runnable{

    private SynchronizedSample o;

    public Test3(SynchronizedSample o){ this.o = o; }

    public void run() {
        try{
            o.ordinaryFunction();
            System.out.println("Test3 hello world");
        }catch (Exception e){}
    }
}

class Test4 implements Runnable{
    public void run(){
        new SynchronizedSample(5);
    }
}

class Test5 implements Runnable{
    private SynchronizedSample o;

    public Test5(SynchronizedSample o){ this.o = o; }

    public void run(){
        o.ordinaryFunction(5);
    }
}
```


#### 情况一

```
        for(int i = 0; i < 10; ++i){
//            pool.execute(new Thread(new Test1()));
//            pool.execute(new Thread(new Test2()));
        }
```

在循环体内，分别测试第一行第二行，结果每隔5秒输出“hello world”，可以得知锁住了整个类。

- synchronized修饰类 静态方法，锁住了类.class
- synchronized修饰 类.Class，锁住了类.class

#### 情况二

```
        for(int i = 0; i < 10; ++i){

            pool.execute(new Thread(new Test3(fixedObject)));
            pool.execute(new Thread(new Test3(new SynchronizedSample())));
        }
```

在循环体内，测试第一行，结果每隔一秒输出“hello world”，可以得知锁住了对象的同步方法；而测试第二行，一下子全输出。

- 同步方法对于同一对象是有效的
- 同步方法对于不同对象是无效的

#### 情况三


```
        pool.execute(new Test1());

        for(int i = 0; i < 10; ++i){
            pool.execute(new Test4());
        }
```

循环体外，先锁住了类.class，然后再次创建对象，10个对象一下子被创建了

- 锁住类的class时，可以创建实例对象
- 锁住类的class时，实例对象也可以调用普通方法


#### 情况四

```
        pool.execute(new Test3(fixedObject));
        for(int i = 0; i < 10; ++i){
           pool.execute(new Test5(fixedObject));
        }
```

锁住一个对象的时候

- 同步方法被调用的时候，非同步方法可以调用，锁的粒度！！！
- 同步方法1调用的时候，同步方法2不可以被调用，实例对象就是那个锁，锁住了同步方法1，就不能拿去锁同步方法2了！


## Synchronized同步机制

对于同步块的实现使用了monitorenter和monitorexit指令，而同步方法则是依靠方法修饰符的ACC_SYNCHRONIZED来完成。无论采用哪种方式，其本质是一个对象的监视器的获取，而这个获取的过程是排他的，也就是同一个时刻只能有一个线程获取到由synchronized所保护对象的监视器。

任何一个对象都拥有自己的监视器，当这个对象由同步块或这个对象的同步方法调用时，执行方法的线程必须先获取到该对象的监视器才可以进入同步块或同步方法，而没有获取到监视器却想执行同步方法或同步块的线程会被阻塞，进入到BLOCKED状态。

![](http://p5s0bbd0l.bkt.clouddn.com/lock1.jpg)

由上图可以看出，任意线程对Object（Object由synchronized保护）的访问，首先要获得Object的监视器。如果获取失败，线程进入同步队列，线程状态变为BLOCKED。当先前获得锁的线程释放了锁，则该释放操作将唤醒阻塞在同步队列中的线程，使其重新尝试对监视器的获取。

## 等待/通知机制

等待方：

- 获取对象的锁
- 如果条件不满足，那么调用对象的wait()方法，被通知后仍要检查条件
- 条件满足则执行相应的逻辑

```
	synchronized(对象){
		while(条件不满足){
			对象的.wait();
		}
	}
```

通知方：

- 获取对象的锁
- 改变条件
- 通知所有等待在对象上的线程

```
	synchronized(对象){
		//改变条件
		对象.notifyAll();
	}
```

#### wait()和notify()方法只能在同步块之中的原因：

如果调用wait()和notify()的方法或代码块所在的线程没有获得该对象的锁，则wait()和notify()之间的多个线程很容易产生竞争条件；通过wait()和notify()可以协调多个线程的执行顺序，如果线程没有获得某个对象的锁，却可以调用该对象的wait()和notify()方法，多个线程之间的协调是很困难的，无法保证正确的执行顺序。

等待/通知机制，是指一个线程A调用了对象O的wait()方法进入等待状态，而另一个线程调用了对象O的notify()方法或者notifyAll()方法，线程A收到通知后从对象O的wait()方法返回，进而执行后续操作。上述两个线程通过对象O来完成交互。

使用wait()和notify()方法需要注意的细节：

- 使用wait()、notify()和notifyAll()时需要先对调用对象加锁 synchronized(Object)；
-  调用wait()方法后，线程状态由RUNNING变成WAITING，并将当前线程放置到对象的等待队列；
- 调用notify()/notifyAll()方法之后，等待线程依旧不会从wait()方法返回，需要调用notify()/notifyAll()的线程释放锁之后，等待线程才有机会从wai()返回；
- notify()方法将等待队列中一个等待线程从等待队列中移到同步队列，而notifyAll()方法将等待队列中的所有线程全部移到同步队列，被移动的线程状态由WAITING变成BLOCKED；
- 从wait()方法返回的前提是获得了调用对象的锁。

![](http://p5s0bbd0l.bkt.clouddn.com/lock2.jpg)

在上图中，WaitThread对象首先获取了对象的锁，然后调用对象的wait()方法，从而放弃了锁并进入了对象的等待队列中，进入等待状态。由于WaitThread释放了对象的锁，NotifyThread随后获取了对象的锁，并调用对象的notify()方法，将WaitThread从等待队列中移到同步队列中，状态变为阻塞状态。

