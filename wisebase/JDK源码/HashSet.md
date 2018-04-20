# HashSet

```java
public class HashSet<E> extens AbstractSet<E>
	implements Set<E>, Cloneable, java.io.Serializable{
	
	static final long serialVersionUID = -5024744406713321676L;
	
	private transient HashMap<E, Object> map;
	
	private static final Object PRESENT = new Object();
	
	public HashSet() { map = new HashMap(); }
	public HashSet(Collection<? extends E> c){
		map = new HashMap<>(Math.max((int)(c.size()/.75f)+1, 16));
		addAll(c);
	}	
	public HashSet(int initialCapacity, float loadFactor){
		map = new HashMap<>(initialCapacity, loadFactor);
	}
	//实际上创造的LinkedHashMap
	public HashSet(int initialCapacity, float loadFactor, boolean dummy){
		map = new LinkedHashMap<>(initialCapacity, loadFactor);
	}
	
	public Iterator<E> iterator(){ return map.keySet().iterator(); }

	public int size(){ return map.size(); }
	
	public boolean isEmpty() { return map.isEmpty(); }
	
	public boolean contains(Object o) { return map.contains(o); }
	
	public boolean add(E e) { return map.put(e, PRESENT) == null; }
	
	public boolean remove(Object o){ return map.remove(o) == PRESENT; }
	
	public void clear() { return map.clear(); }
	
	public Object clone(){
		try{
			HashSet<E> newSet = (HashSet<E>) super.clone();
			newSet.map = (HashMao<E, Object>) map.clone();
			return newSet;
		}catch(CloneNotSupportException e){
			throw new InternalError(e);
		}
	}
}
```