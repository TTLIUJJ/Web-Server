


## HashTable和HashMap

#### NULL键和值

HashMap是支持NULL键和NULL值的，而HashTable会抛出NullPointerException异常。这仅仅是因为HashMap对null做了特殊处理，将null的hashcode值定位了0，从而将其放在哈希表的第0个bucket中。

```java
//以下代码来自 java.util.HashTable
public synchronized V put(K key, V value){
	if(value == null){
		throw new NullPointerException();
	}
	//如果key为NULL，在调用hashcaode()的时候抛出NullPointerExcepton;
	// ...
}

//以下代码来自java.util.HashMap
public V put(K key, V value){
	if(key == null){
		return putForNullKey(value);
	}
}

private V putForNullKey(V value){
	//当key为null的时候，放在table[0]即第0个bucket中
	for(Entry<K, V> e = table[0]; e != null; e = e.next){
		if(e.key == null){
			V oldValue = e.value;
			e.value = value;
			e.recordAccess(this);
			return oldValue;
		}
	}
	modCount++;
	addEntry(0, null, value, 0);
	return null;
}
```

#### 数据结构

HashMap和HashTable都使用哈希表来存储键值对。在数据结构上是基本相同的，都创建了一个继承自Map.Entry的私有的内部类Entry，每一个Entry对象表示存储在哈希表中的一个键值对。

Entry对象唯一表示一个键值对，有四个属性：

- K key 键对象
- V value 值对象
- int hash 键对象的hash值
- Entry entry 指向链表中的下一个Entry对象，null表示当前Entry对象在链表的尾部

插入方式“头插法”，后插入的后面可能用上的几率更大。

![](http://p5s0bbd0l.bkt.clouddn.com/table1.png)

HashMap/HashTable内部用Entry数组实现哈希表，而对于映射到同一个哈希桶（数组的同一个位置）的键值对，使用Entry链表来存储（解决Hash冲突）。

#### 算法

HashTable和HashMap的初始容量大小和每次扩容大小是不同的。

```java
//以下代码来自java.util.HashTable
public HashTable(){
	this(11, 0.75f);	//HashTable默认大小为11
}

protected void rehash(){
	int oldCapacity = table.length;
	Entry<K, V> [] oldMap = table;
	//每次扩容大小为原来的2n+1
	int newCapacity = (oldCapacity << 1) + 1;
	// ... 
}

//以下代码来自java.util.HashMap
//HashMap的默认初始大小为16
static fianl int DEFAULT_INITIAL_CAPACITY = 1 << 4;

void addEntry(int hash, K key, V value, int bucketIndex){
	//每次扩容大小为原来的2倍
	//需要size大于等于阈值并且当前桶被使用了
	if((size >= threshold) && (null != table[bucketIndex])){
		resize(2 * table.length);
	}
}
```

- HashTable的默认初始值大小为11，之后每次扩充为原来的2n+1。
- HashMap默认的初始化大小为16，之后每次扩充为原来的2倍。
- 如果在创建时给定了大小，HashTable会依照给定值创建，而HashMap会将其扩为2的幂次方大小。

HashTable尽量使用素数和奇数，而HashMap总是使用2的幂作为哈希表的大小。当哈希表的大小为素数时，简单的取模运算的结果会更加均匀；然而取模需要大量的除法运算，效率低下，除非模数是2的幂次方，可以直接使用位运算来得到结果。所以

HashMap的大小为2的幂次方，引入了哈希分布不均匀的问题，所以HashMap为了解决这个问题，对哈希算法做了改动。

```java
//以下代码来自java.util.HashTable
int hash = hash(key);
//hash 不能超过Integer.MAX_VALUE 所以要取最小的31bit
int index = (hash & 0x7FFFFFFF) % table.length;

private int hash(Object k){
	return hashSeed ^ k.hashCode();
}

//以下下代码来自java.util.HashMap
int hash = hash(key);
int i = indexFor(hash, table.length);

final int hash(Object k){
	int h = hashSeed;
	if( 0 != h && k instanceof String){
		return sun.misc.Hashing.stringHash32((String) k);
	}
	h ^= k.hashCode();
	h ^= (h >>> 20) ^ (h >>> 12);
	return h ^ (h >>> 7) ^ (h >>> 4);
}

static in indexFor(int h, int length){
	return h & (length - 1);
}
```

由于HashMap使用了2的幂次方，所以在取模运算时不需要做除法，只需要位的与运算就可以了。但是由于引入的hash冲突加剧，在HashMap调用了对象的hashCode方法之后，又做了一些位运算来打散数据。

#### 线程安全性问题

HashTable是线程安全的，而HashMap不是。HashTable线程安全的做法：

```
//以下代码来自java.util.HashTable
public  synchronized V get(Object key){
	Entry tab [] = table;
	int hash = hash(key);
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for(Entry<K, V> e = tab[index]; e != null; e = e.next){
		if(e.hash == hash && e.key.equals(key)){
			return e.value;
		}
	}
	return null;;
}

public Set<K> keySet(){
	if(keySet == null){
		keySet = Collections.synchronizedSet(new KeySet(), this);
	}
	return keySet;
}
```

## ConcurrentHashMap

为什么什么ConcurrentHashMap

- 线程不安全的HashMap：在并发执行put操作的时会引起死循环，因为多线程会导致HashMap的Entry链表形成环形数据结构；

- 效率低下的HashTable：对于put()方法和get()方法都加上synchronized同步锁，竞争越激烈效率越低下；

- 采用分段锁技术的ConcurrentHashMap提高并发访问效率：HashTable在高并发下，多线程竞争同一把锁，造成效率低下。 加入容器内有多把锁，每一把锁分配一部分数据，那么多线程在访问不同分段的数据，不会出现竞争同一把锁的情况。

#### 初始化操作

一个ConcurrentHashMap里面包含着一个Segment数组，Segment的结构和hashMap类似，是一种数组和链表结构。一个Segment里包含一个Entry数组，每个Entry数组中的元素是一个链表结构的元素，当对Entry中的数据进行修改时，必须首先获得与它对应的Segment锁。

通过initialCapacity、loadFactor和concurrencyLevel等几个参数来初始化segment数组、段偏移量segmentShift、段掩码segmentMask和每个segment里的HashEntry数组来实现的。

#### 初始化segments数组

```java
// concurrencyLevel的最大值是（MAX_SEGMENTS）65535，这意味着segments数组的长度最大为65536，对应的二进制为16位。
	if(concurrencyLevel > MAX_SEGMENTS)
		concurrencyLevel = MAX_SEGMENTS;
	
	int sshift = 0;
	int ssize = 1;
	while(ssize < concurrencyLevel){
		++sshift;
		ssize <= 1;
	}
	segmentShift = 32 - sshift;
	segmentMask = ssize - 1;
	this.segments = Segment.newArray(ssize);
```

- ssize即segment数组的长度

上面的代码保证segments数组的长度是2的幂次方，所以必须计算出一个大于等于concurrencyLevel的2的幂次方来作为segments数组的长度。假如concurrencyLevel等于14、15或者16，ssize都会等于16，即容器中锁的个数也是16。

#### 初始化sgementShift和segmentMask

这两个是全局变量在定位segment数组位置的散列算法里使用。

- segmentShift = 32 - sshift
	- shift等于ssize从1向左移位的次数
	- 这里用32是因为ConcurrentHashMap里的hash()方法最大输出数是32位
- segmentMask = ssize - 1
	- 散列运算的掩码
	- ssize最大值为65536，掩码最大为65535，32位都是1

#### 初始化每个segment

- 参数initCapacity是ConcurrentHashMap的初始化容量；
- 参数loadfactor是每个segment的负载因子。

```java
	if(initialCapacity > MAXIMUM_CAPACITY)
		initialCapacity = MAXIMUM_CAPCAITY;
	int c = initialCapacity / ssize;
	if(c * ssize < initialCapacity)
		++c;
	int cap = 1;
	while(cap < c)
		cap <<= 1;
	for(int i = 0; i < this.segments.length; ++i)
		this.segments[i] = new Segment<K, V>(cap, loadFactor);
```

- cap即segment中HashEntry数组的长度，cap的值要么为1，要么是2的n次方
- loadFacotr负载因子默认情况为0.75
	- HashEntry数组中的长度：threshold =(int) cap * loadFactor
	- 默认情况下，ssize等于16, initialCapacity等于16，loadFactor等于0.75，通过运算cap等于1，threshold等于零。
	
	
#### 定位Segment

插入和读取元素的时候，先通过散列算法定位到Segment。

- 首先对元素的hashCode进行一次再散列

```java
private static int hash(int h){
	//.. 一堆 += 和 ^= 算法
	return h ^ (h >>> 16);
}
```

之所以再进行散列，目的是减少散列冲突，是元素能够均匀的分布在不同的Segement上，从而提高容器并发效率。

```java
//  一个散列很差的例子
//  ssize的大小为16,sshiftMask为15
	int h1 = Interger.ParseInt("00001111", 2) & 15;
	int h2 = Interger.ParseInt("00001111", 2) & 15;
	int h3 = Interger.ParseInt("00001111", 2) & 15;
	int h4 = Interger.ParseInt("00001111", 2) & 15;
```

计算结果是四个h的散列值都是15，因为高位并没有参与散列计算。如果通过上面的再散列算法，可以让每一位的数据都散列开，这种再散列能让数字每一位都参与散列运算中，从而减少散列冲突。

ConcurrentHashMap通过以下散列算法定位segment。

```java
final Segment<K, V> segmentFor(int hash){
	segments[(hash >> segmentShift) & segmentMask];
}
```
默认情况下segmentShift为28，因为sshift默认为4，因为concurrenLevel默认为16，segmentMask值15.在散列后数的最大是32位的二进制数据，无符号向右移动28位，意思是让高4位参与到散列运算。

#### ConcurrentHashMap的操作

#### get()操作

先经过一次再散列，通过这个散列值经过散列运算定位segments数组中Segment的位置，然后再使用这个散列值通过散列运算定位到HashEntry数组的位置。

```
public V get(Object key){
	int hash = hash(key.hashCode());
	return segmentFor(hash).get(key, hash);
}
```

get()操作高效在于整个过程不需要加锁，除非读到空值才加锁。这里使用了volatile定义共享变量。在进行get操作并不需要写共享变量，所以不用加锁，并且不会读到过期的值。

- transient volatile int count：用于统计当前Segment数组的大小
- volatile V value：用于存储值的HashEntry的value

定位Segment数组和HashEntry数组的散列算法不一致。其目的是避免两次散列后的值一样，虽然元素在Segment里散列开了，但却没有在HashEntry中散列开。

```java
(hash >> segmentShift) & segmentMask;	//定位Segment
int index = hash & (tab.length - 1);	//定位HashEntry
```

#### put()操作

由于put方法需要对共享变量进行写入操作，所以为了线程安全，在操作共享变量必须加锁。插入操作分两个步骤，第一步判断是否需要对HashEntry数组进行扩容，第二步定位添加元素的位置，然后放入。

- 是否需要扩容：
在插入元素会判断HashEntry数组是否超过容量（threshold），如果超过阈值，则扩容；相对HashMap的插入之后再判断是否需要扩容，显得更合理
- 如何扩容：
容量是原来的两倍，而且只会对某个Segment进行扩容。

#### size()操作

- 步骤1：使用每个segment数组中的count相加，但是在统计期间，可能某个segment的count发生变化；
- 步骤2：在统计期间，锁住每个segment的put、remove和clean方法，效率低下不考虑；
- 步骤3：尝试2次通过不锁住segment的方式统计每个segment大小，如果统计过程中，容器的count发生改变，这时采用加锁这种效率低下的方式。

在统计过程中，如何判断统计过程中容器是否发生改变？使用modCount变量，在put、remove和clean方法里操作元素前都会将变量加1，那么在统计size前后比较这个modCount是否发生改变，从而得知容器大小是否发生改变。