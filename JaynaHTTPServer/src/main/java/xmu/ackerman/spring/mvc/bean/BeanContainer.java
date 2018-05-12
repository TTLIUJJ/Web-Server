package xmu.ackerman.spring.mvc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午4:48 18-4-30
 */
public class BeanContainer {
    private static final Map<String, Object> beanContainer;

    static {
        beanContainer = new HashMap<String, Object>();

    }

    public static Map<String, Object> getBeanContainer(){
        return beanContainer;
    }

    @SuppressWarnings("unchecked")
    public static Object getBean(String beanName){
        if(!beanContainer.containsKey(beanName))
            throw new RuntimeException("can not get bean by " + beanName);

        return beanContainer.get(beanName);
    }

    public static void putBean(String beanName, Object o){
        beanContainer.put(beanName, o);
    }

}
