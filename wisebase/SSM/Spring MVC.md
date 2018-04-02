# Spring MVC

MVC模式是Java的Web应用开发中非常常用的模式。MVC全名是Model View Controller，即模型试图控制器的缩写。

![](http://owj98yrme.bkt.clouddn.com/mvc1.png)

工作流程：

- 用户发送请求到中央控制器DispatcherServlet；
- DispatcherServlet收到请求后调用HandlerMapping处理器映射器；
- HandlerMapping根据请求的URL找到具体的处理器，生成处理器对象以及处理器拦截器（二者组成HandlerExecutionChain），并将其一并返回给DispatcherServlet；
- DispatcherServlet通过HandlerAdapter处理器适配器调用处理器；
- 执行处理器（Controller，也叫后端控制器）；
- Controller执行完成后返回ModelAndView；
- DispatcherServlet将ModelAndView传给ViewReslover；
- ViewReslover解析后返回具体View；
- DispatcherServlet对View进行渲染视图（即将模型数据填充到试图中）；
- DispatcherServlet对用户进行响应。

#### 总结：

SpringMVC 通过DispatcherServlet来调用MVC的三大件：Controller、Model和View。这样就保证MVC的每一个组件只与DispatcherServlet耦合，而彼此之间相互独立，大大降低总程序的耦合性。

Spring MVC使用了适配器模式，DispatcherServlet使用HandlerAdapter来调用不同的Controller，而Controller调用Model产生数据模型，而产生的数据模型再次返回到DispatcherServlet，DispatcherServlet决定使用不同的模板引擎对页面进行渲染。