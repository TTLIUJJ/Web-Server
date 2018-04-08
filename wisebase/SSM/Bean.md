# Bean

## Bean生命周期

![](http://owj98yrme.bkt.clouddn.com/bean2.png)

ApplicationContext容器中,Bean的生命周期流程如上图:

- 容器启动后,会对scope为Sigleton且非懒加载的bean进行实例化;
- 按照Bean定义配置信息,注入所有的属性;
- 如果Bean实现了BeanNameAware接口,会回调该接口的setBeanName()方法,传入该Bean的id,此时该Bean就获得了自己在配置文件中id;
- 如果Bean实现了BeanFactoryAware接口,会回调该接口的setBeanFactory()方法,传入该Bean的BeanFactory,这样该Bean就获得了自己所在的BeanFactory;
- 如果Bean实现了ApplicationContextAware接口,会回调该接口的setApplicationContext()方法,传入该Bean的ApplicationContext,这样该Bean就获得了自己所在的ApplicationContext;
- 如果有Bean实现了BeanPostProcessor接口,会回调该接口的postProcessBeforeInitialzation()方法;
- 如果Bean实现了InitializingBean接口,则会回调该接口的afterPropertiesSet()方法;
- 如果Bean配置了init-method()方法,则会执行init-method配置的方法;
- 如果有Bean实现了BeanPostProcessor接口,则会回调该接口的postProcessAfterInitialization()方法;
- 到此,终于可以正常使用该Bean了,不同的scope的Bean有不同的处理:
	- Singleton: IOC容器会缓存一份该Bean的实例
	- prototype: 每次Bean被调用都会new一个新对象,其生命周期交给调用方管理
- 容器关闭后,如果Bean实现了DisposableBean接口,则会回调该接口的destroy()方法;
-如果Bean配置destroy-method()方法,则会执行detroy-method配置的方法



```java
public class CoffeeBean implements BeanNameAware,
                                BeanFactoryAware,
                                ApplicationContextAware,
                                InitializingBean,
                                DisposableBean{

    private String shit;

    public CoffeeBean(){ System.out.println(">>>>>>>>>>  CoffeeBean()  <<<<<<<<<<"); }
    public String getShit(){ return shit; }
    public void setShit(String shit){
        this.shit = shit;
        System.out.println(">>>>>>>>>>  setName()  <<<<<<<<<<");
    }
    public void myInit(){ System.out.println(">>>>>>>>>>  myInit()  <<<<<<<<<<"); }
    public void myDestroy(){ System.out.println(">>>>>>>>>>  myDestroy  <<<<<<<<<<"); }

    public void setBeanName(String name){ System.out.println(">>>>>>>>>>  setBeanName: " + name + "  <<<<<<<<<<"); }
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException{
        System.out.println(">>>>>>>>>>  setBeanFactory()  <<<<<<<<<<");
    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
        System.out.println(">>>>>>>>>>  setApplicationContext()  <<<<<<<<<<");
    }
    public void afterPropertiesSet() throws Exception{
        System.out.println(">>>>>>>>>>  afterPropertiesSet()  <<<<<<<<<<");
    }
    public void destroy() throws Exception{
        System.out.println(">>>>>>>>>>  destroy()  <<<<<<<<<<");
    }

    public String toString(){ return ">>>>>>>>>>  shit is: " + shit + "  <<<<<<<<<<"; }
}
```

```java
public class MyBeanPostProcessor implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException{
        System.out.println(">>>>>>>>>>  postProcessBeforeInitialization  <<<<<<<<<<");
        return bean;
    }
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException{
        System.out.println(">>>>>>>>>>  postProcessAfterInitialization  <<<<<<<<<<");
        return bean;
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context-3.2.xsd">

    <bean id="coffeeBean"
            destroy-method="myDestroy"
            init-method="myInit"
            class="com.ackerman.reddit.bean.CoffeeBean">
        <property name="shit">
            <value>holy_shit</value>
        </property>
    </bean>

    <!-- 配置自定义的后置处理器 -->
    <bean id="postProcessor" class="com.ackerman.reddit.bean.MyBeanPostProcessor"/>
</beans>
```

```java
public class BeanLifeTest {
    public static void main(String []args){
        System.out.println("start");
        ApplicationContext ac = new ClassPathXmlApplicationContext("ac.xml");
        System.out.println("xml加载完毕");

        CoffeeBean coffeeBean = (CoffeeBean)ac.getBean("coffeeBean");
        System.out.println(coffeeBean);
        ((ClassPathXmlApplicationContext)ac).close();
    }
}
```

输出结果:

```
start
>>>>>>>>>>  CoffeeBean()  <<<<<<<<<<
>>>>>>>>>>  setName()  <<<<<<<<<<
>>>>>>>>>>  setBeanName: coffeeBean  <<<<<<<<<<
>>>>>>>>>>  setBeanFactory()  <<<<<<<<<<
>>>>>>>>>>  setApplicationContext()  <<<<<<<<<<
>>>>>>>>>>  postProcessBeforeInitialization  <<<<<<<<<<
>>>>>>>>>>  afterPropertiesSet()  <<<<<<<<<<
>>>>>>>>>>  myInit()  <<<<<<<<<<
xml加载完毕
>>>>>>>>>>  shit is: holy_shit  <<<<<<<<<<
>>>>>>>>>>  destroy()  <<<<<<<<<<
>>>>>>>>>>  myDestroy  <<<<<<<<<<
```

