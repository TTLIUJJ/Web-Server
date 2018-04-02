# Servlet

Servlet实际上就是一个Java 接口，Servlet接口中定义的是一套处理网络请求的规范，所有实现Servlet的类，都需要实现它那五个方法。

```java
public interface Servlet{
	public String getServletInfo();
	public ServletConfig getServeletConfig();
	public void init(ServletConfig);
	public void service(ServletHttpRequest, ServletHttpResponse);
	public void destroy();
}
```

## Servlet生命周期

所谓生命周期，指的是Servlet容器如何创建Servelt实例、分配其资源、调用其方法和销毁其实例的整个过程。在整个生命周期中，init()和destroy()只会被调用一次，而service()会被多次调用。

#### 实例化

调用其类的无参构造函数，创建Servlet对象，在如下两种情况下会进行对象实例化：

- 当请求到达容器的时候，容器查找该Servlet对象是否存在，如果不存在，才会创建实例对象；

- 容器在启动时，或者部署了某个应用时，会检查web.xml文件中，是否有load-on-starup配置，如果有会根据其优先级（0最高）创建Servelt实例对象。

#### 初始化

为Servelet实例对象分配资源，调用init(ServletConfig)方法，ServletConfig对象可以用来传递Servlet实例对象的初始化参数。

#### 就绪/调用

有请求到达容器的时，容器就调用Servlet对象的service()方法。

HttpServlet的service方法，会根据请求方法自动调用doGet()或者doPost()方法，如果没有重写的话，默认是抛出异常。

#### 销毁

容器根据自身的算法，将不再需要的servlet对象删除掉。

在容器删除实例对象之前，会调用其destroy()方法，释放清理资源。

## JSP和Servlet

JSP文件经过编译之后就变成了Servlet（JSP本质就是Servlet）。