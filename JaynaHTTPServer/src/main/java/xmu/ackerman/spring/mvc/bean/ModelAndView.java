package xmu.ackerman.spring.mvc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午5:01 18-4-30
 */
public class ModelAndView {
    private String uri;
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public ModelAndView(){}

    public ModelAndView(String uri){
        this.uri = uri;
    }

    public String getUri(){
        return uri;
    }

    public ModelAndView setUri(String uri){
        this.uri = uri;
        return this;
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes(){
        return attributes;
    }

    public ModelAndView setAttribute(String key, Object value){
        attributes.put(key, value);
        return this;
    }
}
