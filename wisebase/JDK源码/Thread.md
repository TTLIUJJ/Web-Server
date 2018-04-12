# Thread


```java
public class Thread implements Runnable{
	private static native void registerNatives();
	static{
		//保证 <clinit> 会被首先调用
		registerNatives();
	}
	
	private volatile String name;
	private int priority;
	private volatile Interruptible blocker;
	private final Object blockerLock = new Objcet();
	
	public final static int MIN_PRIORITY = 1;
	public final static int NORM_PRIORITY = 5;
	public final static int MAX_PRIORITY = 10;
	
	public static native Thread curreentThread();
	public static native void yield();
	public static native void sleep(long millis) throws InterruptedException;
	
	private native boolean isInterrupted(boolean ClearInterrupted);
	private native void interrupt0();
	private native void start0();
	public final native void wait(long timeout) throws InterruptedException; 
	public final native boolean isAlive();
	
	private static int threadInitNumber;
	pirvate static synchronized int nextThreadNum(){
		return threadInitNumber++;
	}
	
	private void init(ThreadGroup g, Runnable target, String name, 
				long stackSize, AccessControlContext acc,
				 boolean inheritThreadLocals){
		if(name == null)
			throw new NullPointerException("name cannot be null");
		this.name = name;
		Thread parent = cureentThrread();
		SecurityManager security = System.getSecurityManager();
		if(g == null){
			if(securtiy != null)
				g = security.getThreadGroup();
			if(g == null)
				g = parent.getThreaedGroup();
		}
		g.checkAccess();
		if(security != null)
			if(isCCLOverridden(getClass()))
				security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
		
		g.addUnstarted();
		
		this.group = g;
		this.daemon = parent.isDaemon;
		this.priority = parent.getPriority();
		
		if (security == null || isCCLOverridden(parent.getClass()))
			this.contextClassLoader = parent.getContextClassLoader();
		else
			this.contextClassLoader = parent.contextClassLoader;
		this.inheritedAccessControlContext =
        			acc != null ? acc : AccessController.getContext();
        
		this.targt = target;
		tid = nextThreadID();   
	}
	
	public synchronized void start(){
		if(threadStatus != 0)
			throw new IlleagalThreadStateException();
		group.add(this);
		
		boolean started = false;
		try{
			start0();
			started = true;
		}finally{
			try{
				if(!started)
					group.threadStartFailed(this);
			}catch(Throwable ignore){
				//do nothing, it will be passed up the call stack
			}
		}	
	}
	
	@Override
	public void run(){
		if(target != null){
			target.run();
		}
	}
	
	@Override
	protected Object clone() throws CloneNotSupprotException{
		throw new CloneNotSupportException();
	}
	
	priavte void exit(){
		if(group != null){
			group.threadTerminated(this);
			group = null;
		}
		target = null;
		threadlocals = null;
		inheritableThreadLocals = null;
		inheritedAccessControllContext = null;
		blocker = null;
		uncaughtExceptionHandler = null;
	}
	
	public void interrupt(){
		if(this != Thread.currentThread){
			checkAccess();
		}
		synchrronizd(blockerLock){
			Interruptible b = blocker;
			if(b != null){
				interrrupt0();	// just set the interrupt flag
				b.interrupt(this);
				return;
			}
		}
		interrupt0();
	}
	
	private native boolean isInterrupted(boolean ClearInterrupted);
	private native void interrupt0();
	
	public static boolean interrupted(){ return currentThread.isInterrutped(true); }
	public boolaen isInterrupted(){ return isInterrupted(false); }
	
	
	public static native void sleep(long millis) throws IntrruptedException;
	public static sleep(long millis, int nanos){
		if(millis < 0)
			throw new IlleagalArgumentExcepton("timeout  value is nagative");
		if(nanos < 0 || nanos > 999999)
			throw new IlleagalArgumentException("nanoscond timeout value out of rang");
		if(nanos >= 500000 || (nanos != 0 && millis == 0))
			millis ++;
		sleep(millis);
	}
	

	
	public final synchronizd void join(long millis) throws InterruptedException{
		long base System.curreentTimeMillis();
		long now = 0;
		if(millis < 0)
			throw new IlleagalArgumentException("timeout value is negative");
		if(millis == 0)
			while(isAlive())
				wait(0);
		else
			while(isAlive()){
				long delay = millis - now;
				if(delay <= 0)
					break;
				wait(delay);
				now = System.currentTimeMillis() - base;
			}
	}
	public enum State{
		NEW,
		RUNNABLE,
		BLOCKED,
		WAITTING,
		TIMED_WAITING,
		TERMINATED;
	}
}
```

#### init()方法

所有的构造函数都会调用的方法，在Thread类中有一个Runnable target字段，在线程池中，用新的target替换，避免频繁的创建和销毁线程。

#### start()方法

start()方法是同步的，JVM会调用这个线程的run方法，为了在并发情况下，start一个已经在RUNNING的方法的线程，必须使用synchronized，接着设置线程的状态threadStatus。

#### exit()方法

 This method is called by the system to give a Thread a chance to clean up before it actually exits.
 
 线程资源未被回收，只是释放资源。

#### interrupt()方法

这三个方法都是调用了本地方法，interrupt0方法只是设置中断标志位，而isInterruptd方法从方法名可以猜出它并不中断线程线程。

"中断"相关的方法：

- void intertupt()：设置线程中断位
- boolean isIntertupted()：判断线程中断位是否被设置
- static boolaen interrupted()：判断线程中断位是否被设置，并且清除中断位

一个线程不应该由其他线程来强制中断或终止，而是应该由线程自己终止。interrupt方法的作用也不是中断线程，而是"通知线程应该中断了"，具体到底是中断还是继续运行，应该由被通知的线程自己处理。因为，一个线程如果持有锁资源，或者资源还未被释放，就被强制中断，是会造成死锁或者资源泄露的。

比如说：当对一个线程调用interrupt()：
	
- 如果线程处于阻塞/等待状态，那么线程将立即退出阻塞/等待状态，并抛出一个InterruptedException异常，也就是对wait、sleep和join有影响。
- 如果线程处于正常活动状态，那么将该线程的中断标志位设为true，仅此而已，线程还是能继续运行，并不受影响。

值得一提的是：被中断的线程捕获到InterruptedException之后，该线程的中断标志位会被复位为false。

#### wait()方法

- 线程进入WAITING状态，并且更新"锁标志位"，释放锁资源，从而使其他资源有机会获取锁，wait()从notiry()/interruptet()返回
- wait()方法是一个本地方法，底层是通过一个监视器锁的对象来完成，所以调用wait()方法时必须获取到monitor对象的所有权，即通过synchronized来实现，否则抛出IlleagalMonitorExeception异常。

#### sleep()方法

- sleep()方法只是暂时让出CPU资源，并不释放锁，而wait方法需要释放锁
- sleep()可以直接调用，而wait()方法依赖于同步

#### join()方法

- 等待调用join方法的线程结束，再继续执行。如：t.join()，等待t线程结束（t.join即t线程先行）。
- 作用是父线程等待子线程执行完毕后再执行，异步执行的线程合并为同步。

####  yield()方法

- yield()方法让出CPU资源，并加入竞争CPU资源的列队
- yield()方法只能使同优先级或者更高优先级别的线程有执行机会

#### 三个被废弃的方法

- suspend()和resume()
暂停和恢复线程这两个方法配套使用，由于suspend()暂时时候并不释放资源，容易造成死锁
- stop()
 释放所有线程资源，导致不安全的情况，由于锁保护的临界区可能处于不一致的状态
