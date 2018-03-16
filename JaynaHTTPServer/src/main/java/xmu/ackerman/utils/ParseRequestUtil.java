package xmu.ackerman.utils;

import xmu.ackerman.service.RequestMessage;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:46 18-3-15
 */
public class ParseRequestUtil {
    private static final String INDEX_FILE = "/index.html";
    /**
    * @Description: 解析请求行
    * @Date: 下午1:05 18-3-15
    */
    public static RequestParseState parseHttpRequestLine(RequestMessage rs, byte []buf){
        RequestParseState state = rs.getState();
        RequestParseState error = RequestParseState.PARSE_REQUEST_LINE_EXCEPTION;
        ArrayList<Byte> message = rs.getMessage();
        int major = 0;
        int minor = 0;
        int len = buf.length;
        int pos = rs.getPos(); // 表示在ArrayList<Byte>中的位置
        char ch;
        int p;

        for(p = rs.getPbuf(); p < len; ++p){
            message.add(buf[p]);
            ch = (char)buf[p];

            switch (state){
                case PARSE_START:
                    rs.setMethodBeg(pos);
                    if(ch == '\r' || ch == '\n'){
                        break;
                    }
                    if(exceptionMethodAlpha(ch)){
                        return error;
                    }
                    state = RequestParseState.LINE_METHOD;
                    break;

                case LINE_METHOD:
                    if(ch == ' '){
                        //设置请求方法
                        rs.setMethodEnd(pos);
                        if(!checkMethod(rs)) { return error; }
                        state = RequestParseState.LINE_SPACES_BEFORE_URI;
                        break;
                    }

                    if(exceptionMethodAlpha(ch)){
                        return error;
                    }
                    break;

                case LINE_SPACES_BEFORE_URI:
                    switch (ch){
                        case ' ':
                            break;
                        case '/':
                            rs.setUriBeg(pos);
                            state = RequestParseState.LINE_AFTER_SLASH_IN_URI;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_AFTER_SLASH_IN_URI:
                    //设置请求uri
                    if(ch == ' '){
                        rs.setUriEnd(pos);
                        checkUri(rs);
                        state = RequestParseState.LINE_HTTP;
                    }
                    break;

                case LINE_HTTP:
                    switch (ch){
                        case ' ':
                            break;
                        case 'H':
                            state = RequestParseState.LINE_HTTP_H;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_HTTP_H:
                    switch (ch){
                        case 'T':
                            state = RequestParseState.LINE_HTTP_HT;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_HTTP_HT:
                    switch (ch){
                        case 'T':
                            state = RequestParseState.LINE_HTTP_HTT;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_HTTP_HTT:
                    switch (ch){
                        case 'P':
                            state = RequestParseState.LINE_HTTP_HTTP;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_HTTP_HTTP:
                    switch (ch){
                        case '/':
                            state = RequestParseState.LINE_FIRST_MAJOR_DIGIT;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_FIRST_MAJOR_DIGIT:
                    if(!DIGIT1TO9(ch)) { return error; }
                    major = ch - '0';
                    rs.setMajor(major);
                    state = RequestParseState.LINE_MAJOR_DIGIT;
                    break;

                case LINE_MAJOR_DIGIT:
                    if(ch == '.'){
                        state = RequestParseState.LINE_FIRST_MINOR_DIGIT;
                        break;
                    }
                    if(DIGIT0TO9(ch)) {
                        major = major * 10 + (ch - '0');
                        rs.setMajor(major);
                    }
                    else{ return error; }

                    break;

                case LINE_FIRST_MINOR_DIGIT:
                    if(!DIGIT0TO9(ch)) { return error; }
                    minor = ch - '0';
                    rs.setMinor(minor);
                    state = RequestParseState.LINE_MINOR_DIGIT;
                    break;

                case LINE_MINOR_DIGIT:
                    if(DIGIT0TO9(ch)){
                        minor = minor * 10 + (ch - '0');
                        rs.setMinor(minor);
                        break;
                    }

                    switch (ch){
                        case '\r':
                            state = RequestParseState.LINE_ALMOST_DONE;
                            break;
                        case '\n':
                            // return: parse结束
                            return finishParseRequest(rs, RequestParseState.HEADER_START, pos+1, p+1);
                        case ' ':
                            state = RequestParseState.LINE_SPACES_AFTER_DIGIT;
                            break;
                        default:
                            return error;
                    }
                    break;

                case LINE_SPACES_AFTER_DIGIT:
                    switch (ch){
                        case ' ':
                            break;
                        case '\r':
                            state = RequestParseState.LINE_ALMOST_DONE;
                            rs.setState(state);
                            break;
                        case '\n':
                            //return finish requestHeader
                            return finishParseRequest(rs, RequestParseState.HEADER_START, pos+1, p+1);
                        default:
                            return error;
                    }

                case LINE_ALMOST_DONE:
                    switch (ch){
                        case '\n':
                            return finishParseRequest(rs, RequestParseState.HEADER_START, pos+1, p+1);
                        default:
                            return error;
                    }
            }

            ++pos;
        }

        //未完整的请求, 可能是由于
        //  1. buf太小  2. 数据报文分片
        rs.setPbuf(0);
        rs.setPos(pos);
        rs.setState(state);

        return RequestParseState.PARSE_MORE;
    }

    /**
    * @Description: 解析请求头
    * @Date: 下午1:53 18-3-15
    */
    public static RequestParseState parseHttpRequestHeader(RequestMessage rs, byte []buf){
        RequestParseState state = rs.getState();
        RequestParseState error = RequestParseState.PARSE_REQUEST_HEADER_EXCEPTION;
        ArrayList<Byte> message = rs.getMessage();
        int len = buf.length;
        int pos = rs.getPos();
        char ch;
        int p;

        for(p = rs.getPbuf(); p < len; ++p){
            message.add(buf[p]);
            ch = (char) buf[p];

            switch (state){
                case HEADER_START:
                    if(ch == '\r' || ch == '\n'){ break; }
                    rs.setKeyBeg(pos);
                    state = RequestParseState.HEADER_KEY;
                    break;

                case  HEADER_KEY:
                    switch (ch){
                        case ' ':
                            rs.setKeyEnd(pos);
                            state = RequestParseState.HEADER_SPACES_BEFORE_COLON;
                            break;
                        case ':':
                            rs.setKeyEnd(pos);
                            state = RequestParseState.HEADER_SPACES_AFTER_COLON;
                            break;
                    }
                    break;

                case HEADER_SPACES_BEFORE_COLON:
                    switch (ch){
                        case ' ':
                            break;
                        case ':':
                            state = RequestParseState.HEADER_SPACES_AFTER_COLON;
                            break;
                        default:
                            return error;
                    }
                    break;

                case HEADER_SPACES_AFTER_COLON:
                    if(ch == ' ') { break; }
                    state = RequestParseState.HEADER_VALUE;
                    rs.setValueBeg(pos);
                    break;

                case HEADER_VALUE:
                    switch (ch){
                        case '\r':
                            rs.setValueEnd(pos);
                            state = RequestParseState.HEADER_CR;
                            break;
                        case '\n':
                            rs.setValueEnd(pos);
                            state = RequestParseState.HEADER_CRLF;
                            break;
                    }
                    break;

                case HEADER_CR:
                    switch (ch){
                        case '\n':
                            //封装请求头
                            addHeader(rs);
                            state = RequestParseState.HEADER_CRLF;
                            break;
                        default:
                            return error;
                    }
                    break;

                case HEADER_CRLF:
                    switch (ch){
                        case '\r':
                            state = RequestParseState.HEADER_CRLFCR;
                            break;
                        default:
                            rs.setKeyBeg(pos);
                            state = RequestParseState.HEADER_KEY;
                            break;
                    }
                    break;

                case HEADER_CRLFCR:
                    switch (ch){
                        case '\n':
                            return finishParseRequest(rs, RequestParseState.PARSE_OK, pos+1, p+1);
                        default:
                            return error;
                    }
            }
            ++pos;
        }
        rs.setPbuf(0);
        rs.setPos(pos);
        rs.setState(state);

        return RequestParseState.PARSE_MORE;
    }

    private static RequestParseState finishParseRequest(RequestMessage rs, RequestParseState next, int pos, int pbuf){
        rs.setPbuf(pbuf);
        rs.setPos(pos);
        rs.setState(next);

        return next;
    }

    /**
    * @Description: 不正确的请求方法
    * @Date: 上午11:06 18-3-15
    */
    private static boolean exceptionMethodAlpha(char ch){
        if((ch < 'A' || ch > 'Z') && ch != '_'){ return true; }
        return false;
    }

    private static boolean DIGIT0TO9(char ch){
        if(ch < '0' || ch > '9'){ return false; }
        return true;
    }

    private static boolean DIGIT1TO9(char ch){
        if(ch < '1' || ch > '9'){ return false; }
        return true;
    }

    private static boolean checkMethod(RequestMessage rs){
        ArrayList<Byte> message = rs.getMessage();
        StringBuilder method = new StringBuilder();
        int beg = rs.getMethodBeg();
        int end = rs.getMethodEnd();
        byte b;

        for(int i = beg; i < end; ++i){
            b = message.get(i);
            method.append((char) b);
        }

        String m = method.toString();
        if(m.equals("GET")){
            rs.setMethod("GET");
            return true;
        }
        if(m.equals("POST")){
            rs.setMethod("POST");
            return true;
        }

        return false;
    }

    private static void checkUri(RequestMessage rs){
        try {
            ArrayList<Byte> message = rs.getMessage();
            int beg = rs.getUriBeg();
            int end = rs.getUriEnd();
            StringBuilder uri = new StringBuilder();
            byte b;
            for (int i = beg; i < end; ++i) {
                b = message.get(i);
                uri.append((char) b);
            }
            String URI = uri.toString();
            if(URI.equals("/")){
                URI = INDEX_FILE;
            }

            rs.setUri(URI);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static void addHeader(RequestMessage rs) {
        try {
            ArrayList<Byte> message = rs.getMessage();
            Map<String, String> headers = rs.getHeaders();
            int kBeg = rs.getKeyBeg();
            int kEnd = rs.getKeyEnd();
            int vBeg = rs.getValueBeg();
            int vEnd = rs.getValueEnd();

            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            byte b;

            for (int i = kBeg; i < kEnd; ++i) {
                b = message.get(i);
                key.append((char) b);
            }
            for (int i = vBeg; i < vEnd; ++i) {
                b = message.get(i);
                value.append((char) b);
            }

            headers.put(key.toString(), value.toString());
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
