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

    public HttpRequest(RequestMessage rs){
        this.method = rs.getMethod();
        this.uri = rs.getUri();
        this.protocol = rs.getMajor() + "/" + rs.getMinor();
        this.headers = rs.getHeaders();
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

}
