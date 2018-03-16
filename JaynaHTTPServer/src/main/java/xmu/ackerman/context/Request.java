package xmu.ackerman.context;

import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午9:59 18-3-15
 */
public interface Request {
    public static final String GET = "GET";
    public static final String POST = "POST";

    /**
     * @Description: 获取请求方法
     * @Date: 上午10:04 18-3-15
     */
    public String getMethod();
    public void setMethod(String method);

    /**
    * @Description: 获取协议版本号
    * @Date: 上午10:05 18-3-15
    */
    public String getProtocol();
    public void setProtocol(String protocol);

    /**
    * @Description: 获取请求的uri
    * @Date: 上午10:04 18-3-15
    */
    public String getUri();
    public void setUri(String uri);

    /**
    * @Description: 获取请求头
    * @Date: 上午10:07 18-3-15
    */
    public Map<String, String> getHeaders();
    public void setHeaders(Map<String, String> headers);
}
