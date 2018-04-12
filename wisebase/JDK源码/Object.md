# Object


```java
package java.lang;

public class Object{
	private static native void registerNatives();
	static{ registerNatives(); }	
	
	public native int hashCode();
	
	public boolean equals(Object obj) { return this == obj; }
	
	protected native Object clone() throws ClassNotSupportedException;
	
	public String toString(){ return getClass.getName() + "@" + Integer.toHexString(hashCode()); }
	
	protected void finalize() throws Throwable{}
	
	public final native Class<?> getClass();
	
	public final native void notify();
	
	public final native void notifyAll();
	
	public final native void wait(long timeout) throws InterruptedException;
	
	public final void wait(long timeout, int nanos) throws InterruptedException{
		if(timeout < 0) { throw new IlleagalArgumentException("timeout value is negative"); }
		if(nanos < 0 || nanos > 999999){ throw new IlleagalArgumentException("nanosecond timeout value out of range"); }
		if(timeout > 0){ timeout++; }
		wait(timeout);
	}
	public final void wait() throws InterruptedException{ wait(0); }
}
```

## native修饰符

native修饰的方法就是一个调用非java代码的接口。java底层本来就是用C/C++去写的，所以当然有对应接口直接调用C/C++写的方法，native方法使用非java代码来实现对底层的操作。native方法用于加载文件和动态链接库，在JVM运行时候数据区处于本地方法栈。

##  final修饰符

三种不可以重写的方法：getClass()、notify()和wait()方法

## hashCode & equals

hash值：java中的hashCode方法就是根据一定的规则将与对象相关的信息（比如对象的储存地址，对象的字段等）映射为一个数值，这个数值称为散列值。

重写hashCode()方法的基本规则：

- 在程序运行过程中，同一个对象多次调用hashCode()方法应该返回相同的值，也就是易变的字段不要参与hash运算
- 当两个对象通过equals方法返回true时，两个对象的hashCode值应该要相等
- 在equals方法中，进行对象比较标准的Field，都应该用来计算hashCode值，减少hash碰撞

默认的equals方法是比较两个对象的内存地址，实际上就是比较两个符号是否引用了同一个对象，但是，通常我们比较对象的需求是，判断两个对象的字段是否相同，这就需要重写equals方法了。在JDK中，String和Math类都对equals方法进行了重写。

比较规则一般是先判断两个对象是否引用了同一个地址，再判断hash值是否相等，如果hash值相等，再使用自定义的比较规则进行判断。所以才会说“equals返回true的hashCode一定相等，反之则不成立”。


## clone

- Object中的clone()方法是一个native方法，native方法的效率一般都是远高于java中的普通方法的。
- clone()方法返回的是一个Object对象，使用复制的对象，必须进行强制类型转换。
- 实现clone()的方法的类必须实现Cloneable接口，返回会抛出CloneNotSupportedException。

#### 深拷贝 & 浅拷贝



```java
public class Person  implements Cloneable{
    public String name;
    public int age;

    public Person(String name, int age){
        this.name = name;
        this.age = age;
    }

    public Object clone(){
        try {
            return super.clone();
        }catch (CloneNotSupportedException c){
            //TODO
        }
        return null;
    }
}
```

```java
public class CloneTest {
    public static void main(String []args){
        Person p1 = new Person("ackerman", 11);
        Person p2 = (Person) p1.clone();

        System.out.println("深拷贝： " + !p1.equals(p2));
        System.out.println("浅拷贝： " + p1.name.equals(p2.name));
    }
}
```

结论：本地方法clone是深拷贝，但是String类的字段name是拷贝引用，属于浅拷贝，如果想要让字段name也是深拷贝，重写的clone()方法即可。

