package xmu.ackerman.context;

import xmu.ackerman.service.RequestMessage;

import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:15 18-3-15
 */
public class HttpRequest implements Request {

    private String method;

    private String uri;

    private String protocol;

    private Map<String, String> headers;

    private RequestMessage message;

    private long expireTime;

    public HttpRequest(RequestMessage rs){
        this.method = rs.getMethod();
        this.uri = rs.getUri();
        this.protocol = rs.getMajor() + "/" + rs.getMinor();
        this.headers = rs.getHeaders();
        this.message = rs;
        this.expireTime = 0;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public RequestMessage getMessage() {
        return message;
    }

    public void setMessage(RequestMessage message) {
        this.message = message;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
