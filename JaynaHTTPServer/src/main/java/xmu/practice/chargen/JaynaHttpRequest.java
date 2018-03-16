package xmu.practice.chargen;


import java.util.LinkedList;

public class JaynaHttpRequest {
    public static int MAX_BUFF = 4096;

    public JaynaHttpRequest(){
        this.method = JaynaHTTPCode.OK;

        this.headerList = new LinkedList<JaynaHttpHeader>();
        this.buff = new char[MAX_BUFF];
        this.state = ParseHttpBodyState.SW_START;
        this.state2 = ParseHttpRequestLineState.SW_START;

    }
    private String root;
    private JaynaHTTPCode method;

    private int httpMajor;
    private int httpMinor;

    private int uriStart;
    private int uriEnd;

    private int requestStart;
    private int requestEnd;
    private int methodEnd;

    LinkedList<JaynaHttpHeader> headerList;

    private char []buff;
    private int pos;
    private int last;



    private ParseHttpBodyState state;
    private ParseHttpRequestLineState state2;

    private int curHeaderKeyStart;
    private int curHeaderKeyEnd;
    private int curHeaderValueStart;

    public LinkedList<JaynaHttpHeader> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(LinkedList<JaynaHttpHeader> headerList) {
        this.headerList = headerList;
    }

    public char[] getBuff() {
        return buff;
    }

    public void setBuff(char[] buff) {
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

    private int curHeaderValueEnd;

    public ParseHttpBodyState getState() {
        return state;
    }

    public void setState(ParseHttpBodyState state) {
        this.state = state;
    }

    public int getCurHeaderKeyStart() {
        return curHeaderKeyStart;
    }

    public void setCurHeaderKeyStart(int curHeaderKeyStart) {
        this.curHeaderKeyStart = curHeaderKeyStart;
    }

    public int getCurHeaderKeyEnd() {
        return curHeaderKeyEnd;
    }

    public void setCurHeaderKeyEnd(int curHeaderKeyEnd) {
        this.curHeaderKeyEnd = curHeaderKeyEnd;
    }

    public int getCurHeaderValueStart() {
        return curHeaderValueStart;
    }

    public void setCurHeaderValueStart(int curHeaderValueStart) {
        this.curHeaderValueStart = curHeaderValueStart;
    }

    public int getCurHeaderValueEnd() {
        return curHeaderValueEnd;
    }

    public void setCurHeaderValueEnd(int curHeaderValueEnd) {
        this.curHeaderValueEnd = curHeaderValueEnd;
    }

    public ParseHttpRequestLineState getState2() {
        return state2;
    }

    public void setState2(ParseHttpRequestLineState state2) {
        this.state2 = state2;
    }

    public int getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(int requestStart) {
        this.requestStart = requestStart;
    }

    public int getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(int requestEnd) {
        this.requestEnd = requestEnd;
    }

    public JaynaHTTPCode getMethod() {
        return method;
    }

    public void setMethod(JaynaHTTPCode method) {
        this.method = method;
    }

    public int getMethodEnd() {
        return methodEnd;
    }

    public void setMethodEnd(int methodEnd) {
        this.methodEnd = methodEnd;
    }

    public int getUriStart() {
        return uriStart;
    }

    public void setUriStart(int uriStart) {
        this.uriStart = uriStart;
    }

    public int getUriEnd() {
        return uriEnd;
    }

    public void setUriEnd(int uriEnd) {
        this.uriEnd = uriEnd;
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

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
