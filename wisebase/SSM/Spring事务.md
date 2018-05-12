# Spring事务


## Spring事务中的七种传播方式

Spring事务的传播主要是针对方法中间传递时的事务机制。

现在假设main方法调用了B方法


- PROPAGATION_REQUIRED（默认）
	- 如果main方法有事务，B事务加入其main事务，异常统一回滚
	- 如果main方法没有事务，为B方法新建事务域
	
- PROPAGATION_NESTED：
	- 如果main方法有事务，将方法B作为其子事务
		- main事务rollback和commit响应B事务
		- 事务B的rollbakc和commit不响应main事务
	- 如果main方法没有事务，与PROPAGATION_REQUIRED一致，为方法B创建新事务域
		
- PROPAGATION_NEW：
	- 总是为方法B新建一个事务，在main方法有事务的情况下，先执行B方法事务
	- 两个事务处于不同上下文，异常时各自回滚

- PROPAGATION_SUPPORTS：
	- 如果main方法有事务，方法B进入main事务，异常统一回滚
	- 如果main方法没有事务，main和B均处于非事务上下文中

- PROPAGATION_NOT_SUPPORTED：
	- 如果main方法有事务，则挂起main的事务，方法B不具有事务
	- 如果main方法没有事务，main和B均处于非事务上下文

	
- PROPAGATION_MANDATORY：
	- 如果main方法有事务，方法B进入main事务，异常统一回滚
	- 如果main方法没有事务，抛出异常
	
- PROPAGATION_NEVER：
	- 如果main方法有事务，抛出异常
	- 如果main方法没有事务，main和B均处于非事务上下文


#### 在Spring中使用事务

```java
@Service
public class AdminService{
	@Transactional(propagation = Propagation.REQUIRED, rollback = "Exception.class")
	public void doService(int param){
		//TODO
	}
}
```

异常类型：

- 不可查异常（unchecked exception）：包括RuntimeException及其子类、Error
- 可查的异常：Exception中除了RuntimeException中的异常类

事务回滚的条件：

- 在@Transactionalz注解中不标识rollback和unrollback属性时，事务在抛出不可查的异常是默认回滚
- 为了让可查异常也进行回滚，加上rollback = "Exception.class"
- 让不可查异常不进行回滚，加上unrollback = "RuntimeException.class" 

注意：

- 如果异常被catch捕获了，那么需要重新抛出异常，才能被注解捕获。
- 注解不会被继承，所以事务注解最好标注在具体类