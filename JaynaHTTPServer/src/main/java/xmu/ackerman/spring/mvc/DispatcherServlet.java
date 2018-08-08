package xmu.ackerman.spring.mvc;

import xmu.ackerman.application.ApplicationConfiguration;
import xmu.ackerman.http.context.Context;
import xmu.ackerman.http.context.Request;
import xmu.ackerman.http.context.Response;
import xmu.ackerman.spring.annotation.Controller;
import xmu.ackerman.spring.annotation.RequestMapping;
import xmu.ackerman.spring.ioc.IOCManager;
import xmu.ackerman.utils.ClassUtil;
import xmu.ackerman.utils.ResourceMapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public class DispatcherServlet {
    private IOCManager iocManager = IOCManager.getIocManager();
    private ResourceMapper resourceMapper = ResourceMapper.getResourceMapper();

    // 1.尚未处理
    private HashMap<String, Object> handlerMapper = new HashMap<String, Object>();

    //  <uri, Method>
    private HashMap<String, Method> uriMethod = new HashMap<String, Method>();

    // <uri, ControllerClass>
    private HashMap<String, Object> uriController = new HashMap<String, Object>();



    private static DispatcherServlet dispatcherServlet;

    public static DispatcherServlet getDispatcherServlet(){
        if(dispatcherServlet == null){
            synchronized (DispatcherServlet.class){
                if(dispatcherServlet == null){
                    dispatcherServlet = new DispatcherServlet();
                }
            }
        }
        return dispatcherServlet;
    }

    private DispatcherServlet(){
        try{
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(){
        try{
            Set<Class<?>> classSet = ClassUtil.getClasses(ApplicationConfiguration.class);
            for(Class clazz: classSet){
                if(clazz.isAnnotationPresent(Controller.class)){
                    Method []methods = clazz.getMethods();
                    for(Method method: methods){
                        if(method.isAnnotationPresent(RequestMapping.class)){
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            String uri = requestMapping.value();

                            uriMethod.put(uri, method);
                            System.out.println("?");
                            Object controllerObject = iocManager.getBean(clazz);
                            uriController.put(uri, controllerObject);
                        }
                    }
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String resolver(String uri){
        try{
            Object controller = uriController.get(uri);
            Method method = uriMethod.get(uri);
            return (String)method.invoke(controller);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *    根据HTTP请求，执行以下步骤：
     *        1. uri == > requestMapping
     *                1. true  执行mapper对应的逻辑
     *                2. false 设置responseCode = 404   注: 可扩展 403, 301 ....
     *        2. request == > view mapper执行完逻辑之后
     *                1. 返回对应的view视图, 并写入http response的字段
     *                2. 找不到对应的view视图, 设置responseCode = 404
              3. 退出函数, writeThread写入socket
     */

    public void complexProcess(Context context){
        try{
            process1(context);
            process2(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置 responseCode
    */
    public void process1(Context context){
        try{
            Request request = context.getRequest();
            Response response = context.getResponse();

            String uri = request.getUri();
            Method method = uriMethod.get(uri);

            if(method == null){
                response.setStatusCode(404);
                response.setStatusMsg("404 not found");
                response.setFilePath(resourceMapper.getViewResource("error_404"));
                return;
            }

            Object controller = uriController.get(uri);
            String viewName = (String)method.invoke(controller);

            // requestMapping 返回的 view视图的名称
            String resourcePath = resourceMapper.getViewResource(viewName);
            if(resourcePath == null){
                response.setStatusCode(404);
                response.setStatusMsg("404 not found");
                response.setFilePath(resourceMapper.getViewResource("error_404"));
                return;
            }

            // 终于找到需要返回的资源的路径  需要执行response写入socket了
            response.setFilePath(resourcePath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  根据 资源文件路径 读取 并 写入 response.ResponseBody
     *
     */

    private void process2(Context context){
        try{
            Response response = context.getResponse();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String []args){
        DispatcherServlet dispatcherServlet = new DispatcherServlet();

    }
}
