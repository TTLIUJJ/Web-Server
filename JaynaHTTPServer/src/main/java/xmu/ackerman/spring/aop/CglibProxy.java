package xmu.ackerman.spring.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xmu.ackerman.spring.annotation.After;
import xmu.ackerman.spring.annotation.Before;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CglibProxy implements MethodInterceptor {

    private Object target;

    private Set<Object> enchanceSet;

    private HashMap<Method, Object> befores;
    private HashMap<Method, Object> afters;

    public CglibProxy(Object target, Set<Object> enchanceSet){
        this.target = target;
        this.enchanceSet = enchanceSet;
        init();
    }


    private void init(){
        try{
            befores = new HashMap<Method, Object>();
            afters = new HashMap<Method, Object>();

            for(Object enchance: enchanceSet){
                for(Method method: enchance.getClass().getDeclaredMethods()){
//                    System.out.println(method.getName());
                    if(method.isAnnotationPresent(Before.class)){
                        befores.put(method, enchance);
                    }
                    else if(method.isAnnotationPresent(After.class)){
                        afters.put(method, enchance);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void before(){
        try{
            for(Map.Entry<Method, Object> entry: befores.entrySet()){
                Method method = entry.getKey();
                Object enchanceObject = entry.getValue();
                method.invoke(enchanceObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void after(){
        try{
            for(Map.Entry<Method, Object> entry: afters.entrySet()){
                Method method = entry.getKey();
                Object enchanceObject = entry.getValue();
                method.invoke(enchanceObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Object newProxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//        System.out.println("before");
        before();
//        methodProxy.invoke(o, objects);
        Object res = methodProxy.invokeSuper(o, objects);

        System.out.println("object:" + o.getClass().toString());
        System.out.println("method:" + method.getName());
        System.out.println("args  :" + Arrays.toString(objects));
        System.out.println("methodProxy:" + methodProxy.getSuperName());

//        System.out.println("after");

        after();

        return res;
    }


    public static class A{
        public  void foo(int foo){
            System.out.println("foo");
        }

        public void bar(Double bar) {
            System.out.println("bar");
        }
    }

    public static void main(String []args){
//        CglibProxy proxy = new CglibProxy(new A());
//        A a = (A) proxy.newProxy();
//        System.out.println("A:" + a.getClass().toString());
//        a.foo(10000);
//        a.bar(1.1);
    }

}

