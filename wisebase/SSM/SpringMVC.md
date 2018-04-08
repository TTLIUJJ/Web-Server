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


## 拦截器

用户请求到DispatcherServlet中，DispathcerServlet调用HandlerMapping通过URL查找到对应的Controller和Interceptor，Spring MVC中拦截器是通过HandlerMapping发起的。

![](http://owj98yrme.bkt.clouddn.com/mvc3)

#### 拦截器的实现

Spring MVC实现拦截器的方法之一：实现Spring的HandlerInterceptor接口。

Spring MVC的Interceptor是链式调用的，在一个应用或者说一个请求中可以同时存在多个Interceptor，每个Interceptor的调用会根据它注册顺序依次执行。

```java
public interface HandlerInterceptor {
    boolean preHandle(HttpServletRequest var1, HttpServletResponse var2, Object var3) throws Exception;

    void postHandle(HttpServletRequest var1, HttpServletResponse var2, Object var3, ModelAndView var4) throws Exception;

    void afterCompletion(HttpServletRequest var1, HttpServletResponse var2, Object var3, Exception var4) throws Exception;
}
```

- preHandler()方法：
这个方法在Controller处理业务请求之前被调用，所以在这个方法之中可以进行一些前置初始化操作或者对当前请求的预处理（比如判断请求中是否有有效的免登陆CooKie），不同返回值会有不同影响：
	- 返回false，表示请求结束，后续的Interceptor和Controller都不会被执行
	- 返回true，持续调用下一个Interceptor的preHandler()方法，最后再调用当前请求的Controller处理业务
	
- postHandler()方法：
在Controller请求处理结束之后，DispatcherServlet进行视图返回渲染之前被调用。在这里可以操作Controller处理之后的ModelAndView参数。多个Interceptor的postHandler()的执行顺序与注册顺序相反。

- afterCompletion()：
这个方法在DispatcherServlet渲染视图之后被调用，这个方法主要是用于资源清理工作的。多个Interceptor的afterCompletion()的执行顺序与注册顺序相反。

**部分代码展示：**

```java
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor{
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(hostHolder.getUser() == null){
            //虽然说本次后续的Interceptor和Controller不会被执行
            //但是重定向了请求URI
            httpServletResponse.sendRedirect("/login");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //doSomething
        //比如启动计时器, 记录本次的登录时间
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //释放资源
        //比如关闭计时器
    }
}
```

注册注册器：

```java
@Component
public class RedditWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassPortInterceptor passPortInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	//passPortInterceptor拦截所有请求
        registry.addInterceptor(passPortInterceptor);		
	
	//loginRequeredInterceptor拦截以下两个访问URI
        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns("/cherry")
                .addPathPatterns("/conversation/*");

        super.addInterceptors(registry);
    }
}
```