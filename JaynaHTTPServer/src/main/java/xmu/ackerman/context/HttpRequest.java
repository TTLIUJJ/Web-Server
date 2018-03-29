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

    public HttpRequest(){
        this.message = new RequestMessage();
    }

    public void initRequestAttribute(){
        this.method = message.getMethod();
        this.uri = message.getUri();
        this.protocol = message.getMajor() + "/" + message.getMinor();
        this.headers = message.getHeaders();
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

    public RequestMessage getRequestMessage() {
        return message;
    }

    public void setRequestMessage(RequestMessage message) {
        this.message = message;
    }


}
