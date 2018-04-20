
# ArrayList

ArrayList是一个数组队列，相当于动态数组，与普通数组相比，它的容量可以动态增长。
- 继承AbstractLis，实现了List接口，它是一个数组队列，提供了相关的添加、删除、修改、遍历等功能；
- 实现RandomAccess接口，提供随机访问；
- 实现Cloneable接口，覆盖clone()，能被克隆；
- 实现Java.io.Serializable接口，即支持序列化。

ArrayLists是非线程安全的，线程安全的有Vector和CopyOnWriteArrayList。

ArrayList和Collection的关系图：

![](http://p5s0bbd0l.bkt.clouddn.com/list1.jpg)


#### 源码

```java
package java.util;

public class ArrayList<E> extends AbstractList<E>
	implements List<E>, RandomAccess, Cloneable, Java.io.Serializable
{
	//序列版本号
	private static final long serialVersionUID = 8683452581122892189L;
	//数据的数组
	private transient Object[] elementData;
	//实际数据的数量
	private int size;
	
	//默认构造函数
	public ArrayList(){
		this(10);
	}
	//带容量大小的构造函数
	public ArrayList(int initialCapacity){
		super();
		if(initialCapacity < 0){
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
		this.elementData = new Object[initialCapacity];
	}
	//创建一个包含collection的ArrayList
	public ArrayList(Collection<? extends E> c){
		elementData = c.toArray();
		size = elementData.length;
		if(elementData.getClass != Object[].class){
			elementData = Arrays.copyOf(elementData, size, Object[].class);
		}
	}
	
	//设置当前容量= 实际元素个数
	public void trimToSize(){
		modCount++;
		int oldCapacity = elementData.length;
		if(size < oldCapacity){
			elementData = Arrays.copyOf(elementData, size);
		}
	}
	
	//确定ArrayList的容量
	//如果容量不足容纳当前全部元素，令  新容量=(原始容量×3)/2+1
	public void ensureCapacity(int minCapacity){
		modCount++;
		int oldCapacity = elementData.length;
		if(minCapacity > oldCapacity){
			Object []oldData = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if(newCapacity < minCapactiy){
				//扩容1.5倍还未满足容量需求，使用minCapacity
				newCapacity = minCapacity;
			}
			elementData = Arrays.copyOf(elementData, newCapactiy);
		}
	}
	
	//添加元素
	public boolean add(E e){
		ensureCapacity(size + 1);
		elementData[size++] = e;
		return true;
	}
	
	//返回ArrayList的实际大小
	public int size() { return sieze; }
	
	//返回ArrayList是否包含Object o
	public boolean cantanis(Object o) { return indexOf(o) >= 0; }
	
	//判断是否为空
	public boolean isEmpty(){ return size == 0; }
	
	//正向查找
	public int indexOf(Object o){
		if(o == null){
			for(int i = 0; i < size; ++i)
				if(elementData[i] == null)
					return i;
		}
		else{
			for(int i = 0; i < size; ++i)
				if(o.equals(elementData[i]))
					return i;
		}
		return -1;
	}
	
	//逆向查找
	public int lastIndexOf(Object o){
		if(o == null){
			for(int i = size-1; i >= 0; --i)
				if(elementData[i] == null)
					return i;
		}
		else{
			for(int i = size-1; i >= 0; --i)
				if(o.equals.(elementData[i]))
					return i;
		}
		return -1;
	}

	//返回ArrayList的Object数组，非引用，是一个copy
	public Object[] toArray(){
		return Arrays.copyOf(elementData, size);
	}
	
	//返回ArrayList的模板数组
	public <T> T[] toArray(T []a){
		if(a.length < size){
			//返回copy的数组，a容量不够的时候 
			return (T[]) Arrays.copyOf(elementData, size, a.getClass);
		}
		//a的容量足够大
		System.arraycopy(elementData, 0, a, 0, size);
		if(a.length > size)
			a[size] = null;
		return a;
	}	
	
	//获取index位置的元素值
	public E get(int index){
		RangeCheck(index);
		return (E) elementData[index];
	}
	
	//设置index位置为新值
	public E set(int index, E element){
		RangeCheck(index);
		E oldValue = (E) elementData[index];
		elementData[index] = element;
		return oldValue;
	}
	
	//末尾添加
	public boolean add(E e){
		ensureCapacity(size +1);
		elementData[size++] = e;
		return true;
	}
	
	//添加到指定位置
	public void add(int index, E element){
		if(idnex > size || index < 0){
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
		ensureCapacity(size+1);
		System.arraycopy(elementData, index, elementData, index+1, size-index);
		elementData[index] = element;
		size++;
	}
	
	//删除指定位置的元素
	public E remove(int index){
		RangeCheck(index);
		modCount++;
		E oldValue = (E) elementData[index];
		int numMoved = size - index - 1;
		if(numMoved > 0){
			Sysytem.arraycopy(elementData, index+1, elementData, index, numMoved);
		}
		elementData[--size] = null;
		
		return oldValue;
	}
	
	//删除指定元素
	public boolean remove(Object o){
		if(o == null){
			for(int index = 0; index < size; ++index)
				if(elementData[index] == null){
					fastRemove(index);
					return true;
				}
		}
		else{
			for(int index = 0; index < size; ++index)
				if(o.equals(elementData[index])){
					fastRemove(index);
					return true;
				}
		}
		return false;
	}	
	
	//快速删除第i个元素
	private void fastRemove(int index){
		modCount++;
		int numMoved = size - index -1;
		if(numMoved > 0)
			System.arraycopy(elementData, index+1, elementData, index, numMoved);
		elementData[--size] = null;
	}
	
	//清空
	public void clear(){
		modCount++;
		for(int i = 0; i < size; ++i)
			elementData[i] = null;
		size = 0;
	}
	
	//将集合追加到ArrayList中
	public boolean addAll(Collection<? extneds E> c){
		Object []a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew);
		Sysytem.arraycopy(a, 0, elementData, size, numNew);	//尾部增加
		size += numNew;
		return numNew != 0;
	}
	
	//从index位置开始， 追加集合c
	public boolean addAll(int index, Collection<? extends E> c){
		if(index > size || index < 0)
			throw new IndexOufOfBoundsException("Index: " + index + ", Size: " + size);
		Object [] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew);
		int numMoved = siize - index;
		if(numMoved > 0)
			System.arraycopy(elementData, index, elementData, index+numNew, numMoved);
		System.arraycopy(a, 0, elementData, index, numNew);
		size += numNew;
		return numNew != 0;
	}
	
	//删除fromIndex到toIndex之间的元素
	protected void removeRange(int fromIndex, int toIndex){
		modCount++;
		int numMoved = size - toIndex;
		System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);
		int newSize = size - (toIndex - fromIndex);
		while(size != newSize){
			elementData[--size] = null;
		}
	}
	
	//越界检查
	private void RangeCheck(int index){
		if(index >= size){
			throws new IndexOutOfBoundsException("Index: " + index + ", SIze: " + size);
		}
	}
	
	//克隆函数
	public Object clone(){
		try{
			ArrayList<E> v = (ArrayList<E> )super.clone();
			v.elementData = Arrays.copyOf(elementData, size);
			v.modCount = 0;
			return v;
		}catch(CloneNotSupportedException e){
			throw new InternalError();
		}
	}
	
	//Java.io.Serializable的写入函数
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
		//...		
	}
	//Java.io.Serializable的读取函数
	private void readObject(java.io.ObjectOutputStream s) throws java.io.IOException{
		//...		
	}
}
```

modCount的作用： List有个迭代器，在迭代的过程中，每次都要检查一个modCount是否变化，如果变化了，迭代器处理可能出现不可预知的情况。modCount的作用是迭代器在遍历的时做线程安全检查的。防止一个线程正在迭代遍历，另一个线程修改了这个列表的结构。

