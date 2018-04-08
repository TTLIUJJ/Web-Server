# ThreadLocal

正确理解ThreadLocal不是用来解决对象共享访问问题的，而主要是提供了对象的方法和避免参数传递的复杂。

先了解以下ThreadLocl（线程本地变量）类的四个方法：


```java
 public T get()	//获取线程中保存的变量副本
 public void set(T value)	//设置
 public void remove()	//移除
 protected T initialValue()	//延迟加载，在首次调用get()方法调用
```

在Thread中有一个threadLocals变量，Thread和ThreadLocal属于同一个java.lang包下，ThreadLocl类可以直接访问。

threadLocals变量是ThreadLocalMap类型，以当前ThreadLocal变量为Key，存储T类型的value副本。

初始时，在Thread中threadLocals变为null，当通过ThradLocal变量调用get()或者set()方法，会对Thread中的thradlocals初始化，以当前ThreadLocal变量为Key，以ThreadLocal变量要存储的值为Value，存放到thradLocals中，即存在当前线程中。

```java
ThreadLocal.ThreadLocalMap threadLocals = null;
```

接下来，看一下使用ThradLocl使用get()和方法会发生什么？

```java
public T get(){
	Thread t = Thread.currentThread();
	ThreadLocalMap map = getMap(t);
	if(map != null){
		ThreadLocalMap.Entry e = map.getEntry(this);
		if(e != null){
			return (T)e.value;
		}
	}
	return setInitialValue();
}

ThreadLocalMap getMap(Thread t){
	return t.threadlocals;
}
```

使用ThradLocal的get()方法首先会调用getMap(t)方法，返回的当前线程保存的threadlocals变量：
	
- 如果threadlocals===null，即未被初始化，返回setInitialValue()方法
- 如果threadlocals已经被初始化
	- 并且Key-ThreadLocal：Value-value存在，返回value
	- 如果Key不存在，也是返回setInitialValue()方法

```java

//创建ThreadLocal对象的时候覆写
protected T initialValue() {return null;}

private T setinitialValue(){
	T value = initialValue();	//默认为null
	Thread t = Thread.currentThread();
	ThreadLoclMap map = getMap();
	if(map != null){
		map.set(this, value);
	}
	else{
		createMap(t, value);
	}
	return value;
}
```


```java
static class ThreadLocalMap{
	static class Entry extends WeakReference<ThreadLocl>{
		Object value;
		Entry(ThreadLocl k, Object v){
			super(k);
			this.value = v;
		}
	}
}
```

```java
void createMap(Thread t, T firstValue){
	t.trhreadLocals = new ThreadLocalMap(this, firstValue);
}

```
