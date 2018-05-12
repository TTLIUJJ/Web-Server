package xmu.ackerman.spring.ioc;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午9:16 18-4-30
 */
public class BeanManager {
    private static volatile BeanManager beanManager = null;
    private Map<String, Bean> beanMap = null;

    private BeanManager(){
        try {
            InputStream inputStream = new FileInputStream("applicationContext.xml");
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            Iterator iterator = root.elementIterator("bean");
            while(iterator.hasNext()){
                Element beanElement = (Element) iterator.next();
                System.out.println("id=" + beanElement.attributeValue("id") + ", class:" + beanElement.attributeValue("class"));
                List<Element> elementList = beanElement.elements();
                Bean bean = new Bean();
                for(int i = 0; i < elementList.size(); ++i){
                    Element property = elementList.get(i);
                    String key = property.attributeValue("name");
                    String value = property.attributeValue("value");
                    System.out.println("key=" + key + ", value=" + value);
                }
            }
        }catch (Exception e){
            //TODO
        }
    }

    public BeanManager getBeanManager(){
        if(beanManager == null){
            synchronized (BeanManager.class){
                if(beanManager == null){
                    return new BeanManager();
                }
            }
        }
        return beanManager;
    }

    public Bean getBean(String beanName){
        return beanMap.get(beanName);
    }

    public static void main(String []args){
        BeanManager beanManager = new BeanManager();
    }
}
