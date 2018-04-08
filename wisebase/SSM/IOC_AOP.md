
# Spring IOC

**AOP部分见设计模式：代理模式.md**

IOC是控制反转，所谓反转是指应用本身并不负责依赖对象的创建和维护，而把这个任务交给第三方处理，这是将创建对象的权利交给第三方，即控制权的转移。

传统的的Java SE程序设计，我们直接在对象内部通过new进行创建对象，是程序主动去创建依赖对象，即由IOC容器来控制对象的创建。如此，IOC控制了对象，而这些对象包括POJO和文件等等。

## IOC的实现原理

IOC容器初始化分为三部分：

- bean信息的Resouce定位
- bean信息加载到内存，并可以被实例化
- 将被实例化的bean向IOC容器注册（放入一个map中）

IOC容器初始化完成之后，才有依赖注入（DI）的功能。

## IOC实现的小demo

Bean实例对象：Dog和Master

```java
public interface Pet {
    public void sound(int times);
}

public class Dog implements Pet {

    public void sound(int times){
        while(times -- != 0){
            System.out.print("wang~");
        }
    }
}

public class Master {
    private Pet pet;

    public void setPet(Pet pet) { this.pet = pet; }

    public void invoke(int times){
        System.out.println("do: ");
        pet.sound(times);
        System.out.println();
    }
}
```

IOC容器：实现BeanFactory接口的ClassPathXmlApplicationContext

```java
public interface BeanFactory {
    public Object getBean(String id);
}

public class ClassPathXmlApplicationContext implements BeanFactory{

    private Map<String, Object> beans = new HashMap<String, Object>();

    public ClassPathXmlApplicationContext() throws Throwable{
        //获取bean的xml配置文件
        InputStream inputStream = new FileInputStream("application.xml");
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputStream);
        Element root = document.getRootElement();
        List<Element> list = root.getChildren("bean");

        for(int i = 0; i < list.size(); ++i){
            //通过反射, 一个id对应的class可以实例化为一个Bean
            //         将Bean对象放入Map
            Element e = list.get(i);
            String id = e.getAttributeValue("id");
            String clazz = e.getAttributeValue("class");
            Object o = Class.forName(clazz).newInstance();
            System.out.println("id: " + id + ", class: " + clazz);
            beans.put(id, o);

            //如果Bean对象中有setXXX方法, 将XXX引用放入Bean对象中
            //      这里的XXX和bean都是Map中的对象
            if(e.getChild("property") != null){

                for(Element element : (List<Element>)e.getChildren("property")){
                    String name = element.getAttributeValue("name");
                    Object refObject = beans.get(element.getAttributeValue("ref"));

                    String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = o.getClass().getMethod(methodName, refObject.getClass().getInterfaces()[0]);
                    method.invoke(o, refObject);
                }
            }
        }
    }
    
    // 获取bean实例对象
    public Object getBean(String id){
        return beans.get(id);
    }
```

测试类

```java
public class TestMain {

    public static void main(String []args) throws Throwable{
        try{
          BeanFactory factory = new ClassPathXmlApplicationContext();
          Master master = (Master) factory.getBean("Master");
          master.invoke(3);
        }catch (Exception e){
            e.getStackTrace();
        }
    }
}
```

## IOC和工厂模式的区别

