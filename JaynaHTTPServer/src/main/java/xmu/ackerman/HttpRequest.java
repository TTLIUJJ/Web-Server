package xmu.ackerman;

import xmu.ackerman.zparse_http.HttpHeaderKV;
import xmu.ackerman.zparse_http.HttpRequestMethod;
import xmu.ackerman.zparse_http.ParseRequestState;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class HttpRequest {
    private SelectionKey key;
    private String root;

    private ByteBuffer buff;
    private int pos;
    private int last;

    private ParseRequestState state;
    private HttpRequestMethod method;

    private int requestBeg;
    private int requestEnd;
    private int methodEnd;
    private int uriBeg;
    private int uriEnd;
    private int pathBeg;
    private int begEnd;
    private int queryBeg;
    private int queryEnd;
    private int httpMajor;
    private int httpMinor;

    private LinkedList<HttpHeaderKV> headerList;
    private int curHeaderKeyBeg;
    private int curHeaderKeyEnd;
    private int curHeaderValueBeg;
    private int curHeaderValueEnd;

    //timer

    public HttpRequest(SelectionKey key){
        this.key = key;
        buff = ByteBuffer.allocate(4096);
        state = ParseRequestState.PARSE_START;
        method = HttpRequestMethod.HTTP_METHOD_UNKNOWN;
        headerList = new LinkedList<HttpHeaderKV>();

        //root需要处理
        //TODO
        this.root = "/";
    }


    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public ByteBuffer getBuff() {
        return buff;
    }

    public void setBuff(ByteBuffer buff) {
        this.buff = buff;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public ParseRequestState getState() {
        return state;
    }

    public void setState(ParseRequestState state) {
        this.state = state;
    }

    public HttpRequestMethod getMethod() {
        return method;
    }

    public void setMethod(HttpRequestMethod method) {
        this.method = method;
    }

    public int getRequestBeg() {
        return requestBeg;
    }

    public void setRequestBeg(int requestBeg) {
        this.requestBeg = requestBeg;
    }

    public int getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(int requestEnd) {
        this.requestEnd = requestEnd;
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

    public int getPathBeg() {
        return pathBeg;
    }

    public void setPathBeg(int pathBeg) {
        this.pathBeg = pathBeg;
    }

    public int getBegEnd() {
        return begEnd;
    }

    public void setBegEnd(int begEnd) {
        this.begEnd = begEnd;
    }

    public int getQueryBeg() {
        return queryBeg;
    }

    public void setQueryBeg(int queryBeg) {
        this.queryBeg = queryBeg;
    }

    public int getQueryEnd() {
        return queryEnd;
    }

    public void setQueryEnd(int queryEnd) {
        this.queryEnd = queryEnd;
    }

    public int getHttpMajor() {
        return httpMajor;
    }

    public void setHttpMajor(int httpMajor) {
        this.httpMajor = httpMajor;
    }

    public int getHttpMinor() {
        return httpMinor;
    }

    public void setHttpMinor(int httpMinor) {
        this.httpMinor = httpMinor;
    }

    public LinkedList<HttpHeaderKV> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(LinkedList<HttpHeaderKV> headerList) {
        this.headerList = headerList;
    }

    public int getCurHeaderKeyBeg() {
        return curHeaderKeyBeg;
    }

    public void setCurHeaderKeyBeg(int curHeaderKeyBeg) {
        this.curHeaderKeyBeg = curHeaderKeyBeg;
    }

    public int getCurHeaderKeyEnd() {
        return curHeaderKeyEnd;
    }

    public void setCurHeaderKeyEnd(int curHeaderKeyEnd) {
        this.curHeaderKeyEnd = curHeaderKeyEnd;
    }

    public int getCurHeaderValueBeg() {
        return curHeaderValueBeg;
    }

    public void setCurHeaderValueBeg(int curHeaderValueBeg) {
        this.curHeaderValueBeg = curHeaderValueBeg;
    }

    public int getCurHeaderValueEnd() {
        return curHeaderValueEnd;
    }

    public void setCurHeaderValueEnd(int curHeaderValueEnd) {
        this.curHeaderValueEnd = curHeaderValueEnd;
    }
}
