# SpringMVC 注解

#### ＠Controller

在SpringMVC中，控制器Controller负责处理由DispatcherServlet分发的请求，它把用户请求的数据经过业务处理之后，封装为一个Model，然后再把该Model返回给对应的View进行展示。

使用@Controller标记一个类，然后使用@RequestMapping等其他注解用以定义URL请求和Controller方法之间的映射，这样Controller就能被外界访问到。此外Controller不会直接依赖于HttpServletRequest或者HttpServletResponse等其他HttpServlet对象，它们可以通过Controller的方法参数灵活获取到。

#### @Autowired

@Autowired和@Resource都是用作bean注入时使用，@Resource并不是Spring的注解，它存在于javax.annotation.Resource包中，Spring支持该注解的注入。

相同点：
两者都可以用于写字段和setter方法中。

不同点：

- @Autowried只能通过byType注入，可以用于字段注入和方法中参数的注入

```java
public class SSSS{
	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao){
		this.userDao = userDao;
	}
}

public class SSSS{
	@Autowired
	private UserDao userDao;
}
```

- Autowired按照类型（byType）装配依赖对象，默认情况下它要求依赖对象必须存在，如果允许为null值，可以设置它的required属性为false。如果想要按照名称（byName）来装配，可以结合@Qualifier注解一起使用

```java
@Service("myAdminService")
public class AdminService{
	// ...	
}

@Controller
public class AdminController{
	@Autowired(required = false)
	@Qualifer("myAdminService")
	private AdminService adminService;
	// ...
}

```

- @Resource默认按照byName注入，有两个重要的属性：
	- name：Spring将name属性解析为bean的名字，使用byName自动注入
	- type：Spring将type属性解析为bean的类型，使用byType自动注入
	- 两个属性都没设置，通过反射机制使用byName自动注入

```java
//代码展示了@Resource可以通过name和type进行注入
public class SSSS{
	@Resource(name="myAdminService")
	private AdminService adminService;
}

public class SSSS{
	private AdminService adminService;
	@Resouce(type="AdminService")
	punlic void setAdmnService(Adminservice adminService){
		this.adminService = adminService;
	}
}
```

#### @RequestMapping

@RequestMapping是一个用来处理请求地址映射的注解，可以用于类或者方法上。用于类上表示类中的所有响应请求都是以该地址作为父路径。

＠RequestMapping注解的六个参数：

- value：用来指定请求的路径，可以使用path替代，uri值有三种表达方式：
	- 普通的具体值
	- 含有PathVariable的值
	- 使用正则表达式表示的值

```java
@Controller
@RequesMapping("/ParentPath/{topParam}")
public class AdminController{
	@RequestMapping(value="/deepPath/cherry")
	public String cheery(){
		return "cherry";
	}
	
	@RequestMapping(value="/deepPath/{num:123[0-9]}")
	public String number(@PathVariable("num") int n, @PathVariable("topParam") String aParam ){
		return "number"+ n;
	}
	
	@RequestMapping(path={"/deepPath/apple1", "/deepPath/apple2"})
	public String apple(){
		return "apple";
	}
}
```

- method：指定合法请求的方法
	
```java
@Controller
public class AdminController{
	@RequestMapping(value = "/aha", method = {RequestMethod.GET, RequestMethod.POST})
	public String aha(){
		return "aha";
	}
}
```
	
- consumes：仅处理合法的请求类型（Content-Type）
- produces：仅返回请求中Accept属性头中包含了“text/html”中的请求，同时也表明了返回的类型为“text/html”

```java
@Controller
public class AdminController{
	@RequestMapping(value = "/aha",  consumes = "text/html", produces = "text/html")
	public String aha(){
		return "aha";
	}
}
```

- params：仅处理请求中包含名为“key”，值为“value”的请求

```java
@Controller
public class AdminController{
	@RequestMapping(value = "/aha",  params = "key=value")
	public String aha(){
		return "aha";
	}
}
```

- headers：仅处理请求头header中包含了：“scheme”的请求头属性和对应的值“https”

```java
@Controller
public class AdminController{
	@RequestMapping(value = "/aha",  headers = "scheme="https")
	public String aha(){
		return "aha";
	}
}
```



#### @PathVariable

提取请求URI中的参数

#### @RequestParam

@RequestParam主要用于在SpringMVC后台控制层获取参数，它有三个常用的参数：

- value(name)：表示传入参数的名称
- defaultValue：设置参数默认值
- required：boolean类型，true表示必须传入参数

```java
@Controller
public class AdminController{
	＠RequestMapping(vale="index")
	public String index(@RequestParam(value="index", defaultValue=0, requered=true) int index)
	System.out.println(index);
	return "index";
}
```

#### @RequestHeader

@RequestHeader可以把Request请求头的Header部分的值绑定到方法的参数上

```java
@Controller
public class AdminController{
	public String headerInfo(@RequestHeader("Accept-Encoding") String encoding, @RequestHeader("Keep-Alive") long keepAlive){
		//TODO
	}
}
```

#### @CookieValue

@CookieValue可以把请求头中关于Cookie的值绑定到方法的参数上

```java
@Controller
public class AdminController{
	public String getCookieValue(@CookieValue("JESSIONID") String cookie){
		//TODO
	}
}
```

#### @ModelAttribute

- 运用在方法的参数上，会将客户端传递过来的参数按名称注入到指定的对象中，并且将这个对象自动加入ModelMap中，便于View层使用
- 运用在方法上，会在每一个＠RequestMapping标注的方法前执行，如果有返回值，则会自动将该返回值加入到ModelMap中

```java
@Controller
public class AdminController{
	//个人感觉使用Model参数更为强大, Model相当于是ModelAttribute的数组
	//model.addAttribute("user", user);
	@RequestMapping(vale = "/test1")
	public String test1(@ModelAttribute("user") User user){
		user.setUserName("ackerman");
		return "test1";
	}	
}
```

运用在方法上的@ModelAttribute，会在每一个@RequestMapping注解的方法前执行，若方法有返回值，还会自动加入ModelMap中，这样一来，可以写一个BaseController，用来预先处理@RequestMappping方法都会使用到的参数。

```java
public class BaseController{
	@ModelAttribute("randomMessage")
	private RandomMessage initRandomMessage(@RequestParam("id") int id){
		return RamdomMessage.getRandomMSG(id);
	}
}

@Controller
public class AdminController{
	@RequestMapping(value = "/test2")
	public String test2(Model model){
		//由于有了@ModelAttribute方法, 下面一行代码可省略
		//model.addAttribute("randomMessage", RandomMessage);
		return "test2";
	}
}
```

#### @SessionAttribute

@SeesionAttribute指的是SpringMVC中的Session，在添加一个attribute之后，同时会向HttpSession中添加一个。


#### @ResponseBody

- @ResponseBody注解表示该方法的返回结果直接写入HTTP响应正文中，一般在异步获取数据时使用
- 在使用＠RequestMapping之后，返回值通常解析为跳转路径，加上@ResponseBody之后返回结果直接写入响应正文

#### @Component

@component相当于通用的注解，当不知道一些类归到哪一层的时候使用