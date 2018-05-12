package xmu.ackerman.spring.ioc;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午9:15 18-4-30
 */
public class Bean {
    public static final String SINGLETON = "singleton";
    public static final String PROTOTYPE = "prototype";

    private String name;
    private String className;
    private String scope = SINGLETON;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
