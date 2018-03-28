package xmu.ackerman.service;

import xmu.ackerman.utils.RequestParseState;

import java.util.*;

/**
 * @Author: Ackerman
 * @Description: 获取原生的数据, 封装 赋予HttpRequest
 * @Date: Created in 下午10:38 18-3-15
 */
public class RequestMessage {
    private ArrayList<Byte> message = new ArrayList<Byte>();    //请求原始数据
                                                            //避免在多次读取数据中, 丢失上次读取的数据
    private RequestParseState state;
    private int pos;    //指向message的位置
    private int pbuf;   //指向当前buff的位置,
                        //当buff缓冲太小, buff 至少要读取两次
                        //读取新的buff pbuff要置0

    private int methodBeg;
    private int methodEnd;
    private String method;  //请求方法

    private int uriBeg;
    private int uriEnd;
    private String uri;     //请求资源

    private int major;      //协议版本号
    private int minor;

    private int keyBeg;
    private int keyEnd;
    private int valueBeg;
    private int valueEnd;
    private Map<String, String> headers = new HashMap<String, String>();    //请求头属性


    public RequestMessage(){
        state = RequestParseState.PARSE_START;
    }

    public ArrayList<Byte> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<Byte> message) {
        this.message = message;
    }

    public RequestParseState getState() {
        return state;
    }

    public void setState(RequestParseState state) {
        this.state = state;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPbuf() {
        return pbuf;
    }

    public void setPbuf(int pbuff) {
        this.pbuf = pbuff;
    }

    public int getMethodBeg() {
        return methodBeg;
    }

    public void setMethodBeg(int methodBeg) {
        this.methodBeg = methodBeg;
    }

    public int getMethodEnd() {
        return methodEnd;
    }

    public void setMethodEnd(int methodEnd) {
        this.methodEnd = methodEnd;
    }

    public int getUriBeg() {
        return uriBeg;
    }

    public void setUriBeg(int uriBeg) {
        this.uriBeg = uriBeg;
    }

    public int getUriEnd() {
        return uriEnd;
    }

    public void setUriEnd(int uriEnd) {
        this.uriEnd = uriEnd;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getKeyBeg() {
        return keyBeg;
    }

    public void setKeyBeg(int keyBeg) {
        this.keyBeg = keyBeg;
    }

    public int getKeyEnd() {
        return keyEnd;
    }

    public void setKeyEnd(int keyEnd) {
        this.keyEnd = keyEnd;
    }

    public int getValueBeg() {
        return valueBeg;
    }

    public void setValueBeg(int valueBeg) {
        this.valueBeg = valueBeg;
    }

    public int getValueEnd() {
        return valueEnd;
    }

    public void setValueEnd(int valueEnd) {
        this.valueEnd = valueEnd;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
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
}
