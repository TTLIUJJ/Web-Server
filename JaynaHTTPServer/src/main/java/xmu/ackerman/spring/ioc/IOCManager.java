package xmu.ackerman.spring.ioc;

import xmu.ackerman.application.ApplicationConfiguration;
import xmu.ackerman.spring.annotation.After;
import xmu.ackerman.spring.annotation.Aspect;
import xmu.ackerman.spring.annotation.Autowired;
import xmu.ackerman.spring.annotation.Before;
import xmu.ackerman.spring.aop.CglibProxy;
import xmu.ackerman.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IOCManager {

    // application 包下所有的
    private final HashMap<String, Class> clazzContainer = new HashMap<String, Class>();

    private final HashMap<String, Object> instanceContainer = new HashMap<String, Object>();

    private final HashMap<String, Set<Object>> enchanceContainer = new HashMap<String, Set<Object>>();

    private static IOCManager iocManager ;

    public static IOCManager getIocManager(){
        if(iocManager == null){
            synchronized (IOCManager.class){
                if(iocManager == null){
                    iocManager = new IOCManager();
                }
            }
        }
        return iocManager;
    }

    public Object getBean(String beanName){
        try{
            return instanceContainer.get(beanName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(Class<?> beanClass){
        try{
            return instanceContainer.get(beanClass.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private IOCManager(){
        init();

//        Apple apple = (Apple) instanceContainer.get("xmu.ackerman.application.Apple");
//        apple.sayApple();
    }

    private void init(){
        try{
            Set<Class<?>> classSet = ClassUtil.getClasses(ApplicationConfiguration.class);

            for(Class clazz: classSet){
                String className = clazz.getName();

                Annotation [] annotations = clazz.getAnnotations();
                if(annotations != null && annotations.length > 0) {

                    // 1. 存放包下Bean 的实例
                    initBean(className, clazz);

                    // 2. 存放包下 Bean的class
                    storeClass(className, clazz);
                }
            }
            // 3. 设置Bean属性
            setAutowired();

            // 4. CGLIB动态代理，实现AOP功能
            initAspect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initBean(String className, Class clazz){
        try {
            Object o = clazz.newInstance();
            instanceContainer.put(className, o);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void storeClass(String className, Class clazz){
        try{
            clazzContainer.put(className, clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setAutowired(){
        try{
            for(Map.Entry<String, Class> entry: clazzContainer.entrySet()){
                Class clazz = entry.getValue();
                Object instance = instanceContainer.get(entry.getKey());
                Field [] fields = clazz.getDeclaredFields();

                for(Field field : fields){
                    if(field.isAnnotationPresent(Autowired.class)){
//                        System.out.println("instance: " + instance);
//                        System.out.println("fileName:" + field.getType().getName());
//                        System.out.println("newValue:" + instanceContainer.get(field.getType().getName()));ds

                        Object newValue = instanceContainer.get(field.getType().getName());
                        field.setAccessible(true);
                        field.set(instance, newValue);
//                        * @param obj the object whose field should be modified
//                        * @param value the new value for the field of {@code obj}
//                        field.set();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initAspect(){
        try{
            for(Map.Entry<String, Class> entry: clazzContainer.entrySet()){
                Class clazz = entry.getValue();

                if(clazz.isAnnotationPresent(Aspect.class)) {
//                    System.out.println(clazz.toString());
                    Method []methods = clazz.getMethods();
                    for(Method method: methods){
                        if(method.isAnnotationPresent(Before.class) ||
                            method.isAnnotationPresent(After.class)){

                           String value = "";
                           if(method.isAnnotationPresent(Before.class)){
                               value = method.getAnnotation(Before.class).method();
                           }
                           else {
                               value = method.getAnnotation(After.class).method();
                           }
                           int lastDot = value.lastIndexOf('.');
                           String targetClassName = value.substring(0, lastDot);

                           Set<Object> enchanceSet = enchanceContainer.get(targetClassName);
                           if(enchanceSet == null){
                               enchanceSet = new HashSet<Object>();
                               enchanceContainer.put(targetClassName, enchanceSet);
                           }
                           enchanceSet.add(instanceContainer.get(clazz.getName()));

                           break;
                        }

                    }
                }
            }

            for(Map.Entry<String, Set<Object>> entry: enchanceContainer.entrySet()){
                // 动态代理生成
                CglibProxy cglibProxy = new CglibProxy(instanceContainer.get(entry.getKey()), entry.getValue());
                Object enchancedObejct = cglibProxy.newProxy();
                instanceContainer.put(entry.getKey(), enchancedObejct);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //将生成的动态代理类 更新到 instanceContainer


    public static void main(String args[]){
        try {
            new IOCManager();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
