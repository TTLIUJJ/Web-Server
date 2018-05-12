package xmu.ackerman.spring.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午4:44 18-4-30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    enum RequestMethod{
        GET,
        POST,
        PUT,
        DELETE
    }
    String value();
    String method();
}
