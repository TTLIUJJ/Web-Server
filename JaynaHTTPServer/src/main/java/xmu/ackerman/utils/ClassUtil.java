package xmu.ackerman.utils;

import xmu.ackerman.http.handler.HtmlHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Ackerman
 * @Description: 获取同一路径下的所有类 以及 实现类
 * @Date: Created in 下午8:21 18-3-15
 */
public class ClassUtil {

    /**
    * @Description: 获取C的子类或者实现类
    * @Date: 下午8:44 18-3-15
    */
    public static Set<Class<?>> getAllAssignClasses(Class<?> C)
            throws IOException, ClassNotFoundException{

        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for(Class<?> c : getClasses(C)){
            if(C.isAssignableFrom(c) && !C.equals(c)){
                classSet.add(c);
            }
        }

        return classSet;
    }

    /**
    * @Description: 获取当前路径下的所有类
    * @Date: 下午8:36 18-3-15
    */
    public static Set<Class<?>> getClasses(Class<?> C) throws
            IOException, ClassNotFoundException{

        String bag = C.getPackage().getName();
        String path = bag.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(path);
        File file = new File(url.getFile());

        return getClasses(file, bag);
    }

    /**
    * @Description: 迭代查找类
    * @Date: 下午8:24 18-3-15
    */
    private static Set<Class<?>> getClasses(File dir, String bag) throws ClassNotFoundException{
        Set<Class<?>> classSet = new HashSet<Class<?>>();

        if(!dir.exists()){
            return classSet;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                classSet.addAll(getClasses(f, bag + "." + f.getName()));
            }
            String name = f.getName();
            if(name.endsWith(".class")){
                classSet.add(Class.forName(bag+"."+name.substring(0, name.length()-6)));
            }
        }

        return classSet;
    }

    public static void main(String []args){
        try {
            getAllAssignClasses(HtmlHandler.class);
        }catch (Exception e){

        }
    }

}
