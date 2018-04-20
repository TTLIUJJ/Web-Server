# BitMap

位图，基于位的映射


一个byte是8个bit，如果每一个bit的值就是有或者没有，也就是二进制的0或者1，如果用bit的位置代表数组值有还是没有，以0代表该数值没有出现过，1代表该数值出现过，也是一种描述该数据的方式。

![](http://p5s0bbd0l.bkt.clouddn.com/bitmap1.png)

显示假设有10亿个的整形数据，那么原本需要1GB*4=4GB的内存，那么现在只需要4/32的内存空间，一个占用32bit的数据现在只需要用1bit就可以解决，节省了很大的空间，这样的数据之间是没有关联性的，要是读取，可以使用多线程，时间复杂度也是O(Max/n)，其中Max为byte数组的大小，n为线程数量。


```java
public class BitMap{
	private byte []bits;
	private int capacity;
	
	public BitMap(int capacity){
		this.capacity = capacity;
		bits = new byte[(capacity >> 3) + 1];	//一个字节8位，容量为1,2,...,capacity共capacity个保存的数据
	}
	
	public void add(int num){
		int arrayIndex = num >> 3;	//找到数据对应的字节数组的下标
		int position = num & 0x07;		
		bits[arrayIndex] |= 1 << position;	//找到位数组的下标	
	}
	
	public boolean contains(int num){
		int arrayIndex = num >> 3;
		int position = num & 0x07;
		return (bits[arrayIndex] & (1 << position)) == 1;
	}
	
	public void clear(int num){
		int arrayIndex = num >> 3;
		int postion = num & 0x07;
		bits[arrayIndex] &= ~(1 << postion);
	}	
}
```