# String 

```java
package java.lang;

public final class String implements java.io.Serializable, Comparable<String>, CharSequence{
	//底层本身就是char数组构成的，并且该字符数组是不可变的
	private final char []value;
	
	//持有一个整形hash值，可以用来做预比较
	private int hash;
	
	//实现了序列化接口，所以拥有序列化ID
	private static final long serialVersionUID = 6849794470754667710L;
	
	public int hashCode(){
		int h = hash;
		if(hash == 0 && value.length > 0){
			char []val  = value;
			for(int i = 0; i < value.length; ++i){
				h = 31 * h + val[i];
			}
		}
		return h;
	}
	
	public boolean equals(Object anObject){
		//首先比较的是引用
		if(this == anObject){ return true; }
	
		if(anObject instance String){
			String anotherString = (String) anObject;
			int n = value.length;
			if(n == anotherString.value.length){
				char []v1 = value;
				char []v2 = anotherString.value;
				int i = 0;
				while(n-- != 0){
					if(v1[i] != v2[i]) return false;
					i++;
				}
				return true;
			}
		}
		return false;
	}
	
	public native String intern();
}
```

## intern()方法	

字符串池（有时我们也称为常量池），它的存在是能够使用String.intern()方法原因。在许多的标准中，JDK1.6中禁止使用String.inter()方法，因为频繁使用，字符串池会失去控制，并且得到OutOfMemoryException。Oracle在JDK1.7中重新实现了字符串池。

#### intern() in JDK1.6

所有intered的String对象存储在PermGen中（堆中的固定大小的一部分，主要用于存储加载的类对象和被字符串池），（HotSpot虚拟机将永久代扩展到方法区）。

在JDK1.6中字符串池最大的问题是它的位置-永久代。根据参考资料，永久代默认的大小位于32M至96M之间，依赖于平台可以扩大。永久代具有固定尺寸并且在运行时候不能扩展，它使用参数XX:MaxPermSize=96m。这个固定尺寸的限制需要我们小心的使用String.intern()方法，最好不要对不能控制的字符串使用intern()方法，这就是为什么JDK1.6之中，大部分使用手动管理map的方式来实现字符串池。

字符串池的大小受到永久代大小的限制。

#### intern in JDK1.7

在JDK1.7中，字符串池被移动到堆中，意味着字符串池不必被限制在固定的内存中，所有的字符串对象都和其他普通对象一样位于堆中。所有在字符串池的对象如果在没有任何引用指向它们的时候就会在GC中被回收，对比于JDK1.6中，字符串池存储永久代。

字符串池是使用一个的桶带链表的hashMap，其中桶的大小是固定。

配置字符串常量池的大小：-XX:StringTableSize=N，N是字符串池Map中桶的大小，一般取质数会有较好表现，默认大小为1009,在JDK1.6这个参数没什么用，因为字符串池在永久代依然是一个受限制的大小。而在JDK1.7之后，字符串池被限制在一个更大的堆内存中，意味着可以预先设置好常量池的大小，一般取8-16M。

#### intern in JDK1.8

在JDK1.8版本中，依然接受-XX:StringTableSize=N，唯一不同的是池默认大小增加到25-50K。记得N靠近质数会有较好的性能。

#### 字符串的操作

- new操作和intern()操作

![](http://p5s0bbd0l.bkt.clouddn.com/intern1.png)

![](http://p5s0bbd0l.bkt.clouddn.com/intern2.png)

![](http://p5s0bbd0l.bkt.clouddn.com/intern3.png)


- 字符串'+'操作，在编译期会直接合并为一个字符串，比如：

```java
	String str = "JJ" + "AA"；
	//在编译阶段会等效于如下的代码
	String str = "JJAA";
	//如果str存在于字符串常量池，直接返回引用，反之创建对象后再返回引用
```

- 字符串字面量 + String对象，会调用StringBuilder.append()在堆上创建新的对象，比如：

```java
	String str0 = "JJ";
	String str = str0 + "AA";
	//在编译阶段相当于如下代码
	StringBuilder sb = new StringBuilder();
	sb.appedn(str0).append("AA");
	str = sb.toString();
```

- JDK1.7之后，intern()方法还是首先会去常量池访问字符串是否存在，如果不存在，不会再将字符串拷贝到字符串池，而是直接拷贝字符串的引用，放到字符串池。


![](http://p5s0bbd0l.bkt.clouddn.com/intern4.png)


```java
	String s3 = new String("jay") + new String("na");
	s3.intern();	//将s3对象的引用拷贝到常量池
	String s4 = "jayna";
	System.out.println(s3 == s4 + "(true)");    //两个都是引用s3的地址
	
	String s3 = new String("jay") + new String("na");
	String s4 = "jayna";
	s3.intern();
	System.out.println(s3 == s4 + "(false)");    //s4在字符串池引用的不是s3的地址	
```

#### 总结

HotSpot VM的StringTable的本体在native memory里。它持有String对象的引用而不是String对象的本体。被引用的String还是在Java heap里。一直到JDK6，这些被intern的String在permgen里，JDK7开始改为放在普通Java heap里。

#### 常见的笔试题

```java
	String s1 = "abc";	//在堆上创建字符串后，将引用放入常量池
	String s2 = "abc";
	System.out.println(s1 == s2 + "(true)");	//常量池中的同一个引用
	
	String s1 = new String("abc");
	String s2 = new String("abc");
	System.out.println(s1 == s2 + "(false)");	//两个引用不同的堆对象
	
	String s0 = "abc";
	String s1 = "a";
	String s2 = "bc";
	String s3 = s1 + s2;
	System.out.println(s1 == s3 + "(false)");	//两个引用不同的对象
	
	String s0 = "abc";
	final String s1 = "a";
	final String s2 = "bc";	//final字符串的引用在编译阶段就进入常量池
	String s3 = s1 + s2;	//那么相当于 s3 = "a" + "bc"; --> s3 = "abc";
	System.out.println(s0 == s3 + "(true)");	// 引用同一个常量池对象
	
	String s1 = new String("abc");
	String s2 = "abc";
	String s3 = new String("abc");
	System.out.println(s1 == s2.intern() + "(false)");	//两个不同的引用
	System.out.println(s1 == s3.intern() + "(fasle)");	//不同的引用
	System.out.println(s2 == s3.intern() + "(true)" );	//同一个引用，s2
```