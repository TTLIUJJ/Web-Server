package xmu.ackerman.zparse_http;

import xmu.ackerman.HttpRequest;
import xmu.ackerman.HttpResponse;
import xmu.ackerman.utils.FilePath;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ParseRequestUtil {
    private static int MAX_BUF = 4096;

    public static void doRequest(HttpRequest request) {
        SelectionKey key = request.getKey();
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = request.getBuff();
        String root = request.getRoot();
        StringBuffer fileName = new StringBuffer();
        ParseRequestState state;

        int remainSize = 0;
        int nRead = 0;

        try {
            nRead = client.read(buffer);
            if(parseHttpRequestLine(request) != JaynaHttpCode.OK){
                return;
            }

            if(parseHttpRequestBody(request) != JaynaHttpCode.OK){
                return;
            }

            if(request.getMethod() == HttpRequestMethod.HTTP_METHOD_UNKNOWN){
                return;
            }
            //请求已被解析,
            //接下来 分析key-value对应需要分配的资源
            //      送回index.html等页面
            HttpResponse response = new HttpResponse(request.getKey());
            parseUri(request, response);

            System.out.println("11111");
            if(!checkRequestSource(response)){
                // 找不到请求, 直接返回错误的页面
                return;
            }
            else{

            }
            System.out.println("22222");

            // 静态资源 server_static
            key.attach(response);
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
            //TODO
            System.out.println("doRequest: " + e);
        }

    }

    //ByteBuffer
    // -position(): 表示目前可读写的位置, 向buffer执行读写后, position自动增减相应的数值
    // -limit(): 表示可以读写的边界, 当position == limit, 表示缓冲区读完/写满
    // -mark(): 用于对当前position的标记

    // public final Buffer flip(){  为了读取数据, 使用flip()
    //      limit = position;       flip(): 在向buffer写入数据之后, position为缓冲区的刚刚写入的
    //      position = 0;                   数据的最后一个位置.
    //      mark = -1;                      flip()方法将limit置位position,
    //      return this;                    这样, 使用get()读取缓冲区 [0, limit]中的有效数据
    // }

    // public final Buffer clear(){ 为了写入数据, 使用clear()
    //      position = 0;           clear(): 将position置位缓冲区的头部, 安全写入数据
    //      limit = capacity;                [0, capacity].
    //      mark = -1;                       clear()并不清除数据, 而是直接写入覆盖
    //      return this;
    // }

    // public final boolean hasRemaining(){
    //      return position < limit;        判断缓冲区是否还有数据可读
    // }

    public static JaynaHttpCode parseHttpRequestLine(HttpRequest request){
        ParseRequestState state = request.getState();
        ByteBuffer buffer = request.getBuff();
        buffer.flip();

        byte b;
        char ch;
        int p = 0;
        while(buffer.hasRemaining()){
            b = buffer.get();
            ch = (char)b;
            p = buffer.position() - 1;

            switch (state){
                case PARSE_START:
                    //requestBeg == methodBeg
                    request.setRequestBeg(p);
                    if(ch == '\r' || ch == '\n'){
                        break;
                    }
                    if((ch < 'A' || ch > 'Z') && ch != '_'){
                        return JaynaHttpCode.PARSE_HTTP_METHOD_EXCEPTION;
                    }
                    state = ParseRequestState.HEADER_METHOD;
                    break;

                case HEADER_METHOD:
                    if(ch == ' '){
                        request.setMethodEnd(p);
                        if(checkMethod(request, "GET")){
                            request.setMethod(HttpRequestMethod.HTTP_GET);
                        }
                        else if(checkMethod(request, "POST")){
                            request.setMethod(HttpRequestMethod.HTTP_POST);
                        }
                        else if(checkMethod(request, "HEAD")){
                            request.setMethod(HttpRequestMethod.HTTP_HEAD);
                        }
                        else{
                            request.setMethod(HttpRequestMethod.HTTP_METHOD_UNKNOWN);
                        }
                        state = ParseRequestState.HEADER_SPACES_BEFORE_URI;
                        break;
                    }

                    if((ch < 'A' || ch > 'Z') && ch != '_'){
                        return JaynaHttpCode.PARSE_HTTP_METHOD_EXCEPTION;
                    }
                    break;

                case HEADER_SPACES_BEFORE_URI:
                    if(ch == '/'){
                        request.setUriBeg(p+1);
                        state = ParseRequestState.HEADER_AFTER_SLASH_IN_URI;
                    }
                    else if(ch == ' '){
                        // do nothing
                    }
                    else{
                        return JaynaHttpCode.PARSE_HTTP_REQUEST_EXCEPTION;
                    }

                    break;

                case HEADER_AFTER_SLASH_IN_URI:
                    if(ch == ' '){
                        request.setUriEnd(p);
                        state = ParseRequestState.HEADER_HTTP;
                    }
                    break;

                case HEADER_HTTP:
                    if(ch == ' '){
                        // do nothing
                    }
                    else if(ch == 'H'){
                        state = ParseRequestState.HEADER_HTTP_H;
                    }
                    else{
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    break;

                case HEADER_HTTP_H:
                    if(ch == 'T'){
                        state = ParseRequestState.HEADER_HTTP_HT;
                    }
                    else{
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    break;

                case HEADER_HTTP_HT:
                    if(ch == 'T'){
                        state = ParseRequestState.HEADER_HTTP_HTT;
                    }
                    else{
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    break;

                case HEADER_HTTP_HTT:
                    if(ch == 'P'){
                        state = ParseRequestState.HEADER_HTTP_HTTP;
                    }
                    else{
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    break;

                case HEADER_HTTP_HTTP:
                    if(ch == '/'){
                        state = ParseRequestState.HEADER_FIRST_MAJOR_DIGIT;
                    }
                    else{
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    break;

                case HEADER_FIRST_MAJOR_DIGIT:
                    if(ch < '1' || ch > '9'){
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    request.setHttpMajor(ch - '0');
                    state = ParseRequestState.HEADER_MAJOR_DIGIT;
                    break;

                case HEADER_MAJOR_DIGIT:
                    if(ch == '.'){
                        state = ParseRequestState.HEADER_FIRST_MINOR_DIGIT;
                        break;
                    }
                    else if(ch < '0' || ch > '9'){
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    request.setHttpMajor(request.getHttpMinor() * 10 + (ch - '0'));
                    break;

                case HEADER_FIRST_MINOR_DIGIT:
                    if(ch < '0' || ch > '9'){
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    request.setHttpMinor(ch - '0');
                    state = ParseRequestState.HEADER_MINOR_DIGIT;
                    break;

                case HEADER_MINOR_DIGIT:
                    if(ch == '\r'){
                        state = ParseRequestState.PARSE_ALMOST_DONE;
                        break;
                    }
                    else if(ch == '\n'){
                        return finishHeaderParse(request);
                    }
                    else if(ch == ' '){
                        state = ParseRequestState.HEADER_SPACES_AFTER_DIGIT;
                        break;
                    }
                    else if(ch < '0' || ch > '9'){
                        return JaynaHttpCode.PARSE_HTTP_PROTOCOL_EXCEPTION;
                    }
                    request.setHttpMinor(request.getHttpMajor()*10 + (ch-'0'));
                    break;

                case HEADER_SPACES_AFTER_DIGIT:
                    switch (ch){
                        case ' ':
                            break;
                        case '\r':
                            state = ParseRequestState.PARSE_ALMOST_DONE;
                            break;
                        case '\n':
                            return finishHeaderParse(request);
                        default:
                            return JaynaHttpCode.PARSE_HTTP_HEADER_EXCEPTION;
                    }
                    break;

                case PARSE_ALMOST_DONE:
                    request.setRequestEnd(p);
                    switch (ch){
                        case '\n':
                            return finishHeaderParse(request);
                        default:
                            return JaynaHttpCode.PARSE_HTTP_HEADER_EXCEPTION;
                    }
            }
        }
        request.setPos(p);
        request.setState(state);
        return JaynaHttpCode.PARSE_MOVING;

    }

    public static JaynaHttpCode parseHttpRequestBody(HttpRequest request){
        ByteBuffer buffer = request.getBuff();
        ParseRequestState state = request.getState();

        char ch;
        byte b;
        int p = 0;
        while(buffer.hasRemaining()){
            b = buffer.get();
            ch = (char) b;
            p = buffer.position()-1;

            switch (state){
                case PARSE_START:
                    if(ch == '\r' || ch == '\n'){ break; }
                    request.setCurHeaderKeyBeg(p);
                    state = ParseRequestState.BODY_KEY;
                    break;

                case BODY_KEY:
                    switch (ch){
                        case ' ':
                            request.setCurHeaderKeyEnd(p);
                            state = ParseRequestState.BODY_SPACES_BEFORE_COLON;
                            break;
                        case ':':
                            request.setCurHeaderKeyEnd(p);
                            state = ParseRequestState.BODY_SPACES_AFTER_COLON;
                            break;
                    }
                    break;

                case BODY_SPACES_BEFORE_COLON:
                    switch (ch){
                        case ' ':
                            break;
                        case ':':
                            state = ParseRequestState.BODY_SPACES_AFTER_COLON;
                            break;
                        default:
                            return JaynaHttpCode.PARSE_HTTP_REQUEST_EXCEPTION;
                    }
                    break;

                case BODY_SPACES_AFTER_COLON:
                    if(ch == ' '){
                        break;
                    }
                    state = ParseRequestState.BODY_VALUE;
                    request.setCurHeaderValueBeg(p);
                    break;

                case BODY_VALUE:
                    switch (ch){
                        case '\r':
                            request.setCurHeaderValueEnd(p);
                            state = ParseRequestState.BODY_CR;
                            break;
                        case '\n':
                            request.setCurHeaderValueEnd(p);
                            state = ParseRequestState.BODY_CRLF;
                            break;
                    }
                    break;

                case BODY_CR:
                    switch (ch){
                        case '\n':
                            state = ParseRequestState.BODY_CRLF;
                            addHttpHeaderKV(request);
                            break;
                        default:
                            return JaynaHttpCode.PARSE_HTTP_REQUEST_EXCEPTION;
                    }
                    break;

                case BODY_CRLF:
                    switch (ch){
                        case '\r':
                            state = ParseRequestState.BODY_CRLFCR;
                            break;
                        default:
                            request.setCurHeaderKeyBeg(p);
                            state = ParseRequestState.BODY_KEY;
                            break;
                    }
                    break;

                case BODY_CRLFCR:
                    switch (ch){
                        case '\n':
                            return finishBodyParse(request);
                        default:
                            return JaynaHttpCode.PARSE_HTTP_REQUEST_EXCEPTION;
                    }

            }
        }
        request.setPos(p);
        request.setState(state);
        return JaynaHttpCode.PARSE_MOVING;
    }



    private static JaynaHttpCode finishHeaderParse(HttpRequest request){
        ByteBuffer buffer = request.getBuff();


        request.setPos(buffer.position());
        request.setState(ParseRequestState.PARSE_START);
        return JaynaHttpCode.OK;
    }

    private static JaynaHttpCode finishBodyParse(HttpRequest request){
        ByteBuffer buffer = request.getBuff();

        //同步position
        //当遇上 "\r\n\r\n", buffer.position()已经读取到尾后一个字符了
        //而此时, 解析完毕了, request.pos应该和buffer.position同步
        request.setPos(buffer.position());
        request.setState(ParseRequestState.PARSE_START);
        return JaynaHttpCode.OK;
    }

    // ByteBuffer.get(index) 绝对读 不会改变position的位置
    private static boolean checkMethod(HttpRequest request, String method){
        int len = request.getMethodEnd() - request.getRequestBeg();
        ByteBuffer buffer = request.getBuff();
        int beg = request.getRequestBeg();

        if(len != method.length()){
            return false;
        }
        for(int i = 0; i < len; ++i){
            char ch = (char)buffer.get(i+beg);
            char expected = method.charAt(i);
            if(ch != expected){
                return false;
            }
        }

        return true;
    }

    private static void addHttpHeaderKV(HttpRequest request){
        HttpHeaderKV kv = new HttpHeaderKV(request.getBuff(),
                request.getCurHeaderKeyBeg(),
                request.getCurHeaderKeyEnd(),
                request.getCurHeaderValueBeg(),
                request.getCurHeaderValueEnd());

        request.getHeaderList().add(kv);
    }



    //目前只考虑非参数的请求
    private static void parseUri(HttpRequest request, HttpResponse response){
        ByteBuffer buffer = request.getBuff();
        int beg = request.getUriBeg();
        int end = request.getUriEnd();
        int qMark = beg;
        char ch;

        //自右往左找到 第一个'?'的位置, 参数的界定点
        while(qMark < end){
            ch = (char) buffer.get(qMark);
            if(ch == '?') { break; }
            ++qMark;
        }

        //将 '?'之前的内容加入filename
        StringBuffer fileName = new StringBuffer("/");
        for(int i = beg; i < qMark; ++i){
            ch = (char)buffer.get(i);
            fileName.append(ch);
        }

        //在请求中找到最后一个 '/', 找到请求资源的开始处

        int lastSlash = fileName.length()-1;
        while(lastSlash >= 0){
            ch = (char)buffer.get(lastSlash);
            if(ch == '/') { break; }
            --lastSlash;
        }

        // 默认请求的是index.html
        if(fileName.charAt(fileName.length()-1) == '/'){
            fileName.append("index.html");
        }

        response.setFileName(fileName.toString());
        System.out.println("------------" + fileName + "---------");
    }


    private static boolean checkRequestSource(HttpResponse response){

        FilePath filePath = FilePath.getResourcesFile();
        String filename = response.getFileName();

        //处理文件找不到的错误
        if(!filePath.containsFile(filename)){
            response.setStatus(404);
            return false;
        }

        response.setResponseFile(filePath.getFilePath(filename));
        //处理文件资源需要权限的错误
        //TODO

        return true;
    }
//    public static void main(String []args){
//
//        String REQUEST_BODY =
//                "Host: 127.0.0.1:8080\r\n" +
//                        "Connection: keep-alive\r\n" +
//                        "Pragma: no-cache\r\n" +
//                        "Cache-Control: no-cache\r\n" +
//                        "Upgrade-Insecure-Requests: 1\r\n" +
//                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36\r\n" +
//                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n" +
//                        "Accept-Encoding: gzip, deflate, br\r\n" +
//                        "Accept-Language: zh-CN,zh;q=0.8\r\n" +
//                        "Cookie: csrftoken=q4LiiBxgI1L8ZvOe13TYHgRK8zMma4MB; Hm_lvt_512065947708a980c982b4401d14c2f5=1510677245,1510766058,1510794879,1510831792\r\n\r\n";
//
//
//        JaynaHttpRequest httpBody = new JaynaHttpRequest();
//
//        httpBody.setBuff(REQUEST_BODY.toCharArray());
//        httpBody.setLast(REQUEST_BODY.length());
//        parseHttpBody(httpBody);
//        LinkedList<JaynaHttpHeader> headers = httpBody.getHeaderList();
//        char []buff = httpBody.getBuff();
//        for(JaynaHttpHeader header : headers){
//            for(int i = header.getKeyStart(); i < header.getKeyEnd(); ++i){
//                System.out.print(buff[i]);
//            }
//            System.out.print(": ");
//            for(int i = header.getValueStart(); i < header.getValueEnd(); ++i){
//                System.out.print(buff[i]);
//            }
//            System.out.print("\n");
//        }
//
//
//        String REQUEST_HEADER = "GET /index.html HTTP/1.1\r\n";
//
//        JaynaHttpRequest httpHeader = new JaynaHttpRequest();
//
//        httpHeader.setBuff(REQUEST_HEADER.toCharArray());
//        httpHeader.setLast(REQUEST_HEADER.length());
//        parseHttpRequestLine(httpHeader);
//
//        System.out.println(httpHeader.getMethod());
//        System.out.println(httpHeader.getHttpMajor() + "/" + httpHeader.getHttpMinor());
//        System.out.print("request source: ");
//        for(int i = httpHeader.getUriStart(); i < httpHeader.getUriEnd(); ++i){
//            char []buff2 = httpHeader.getBuff();
//            System.out.print(buff2[i]);
//        }
//        System.out.println("\r\n");
//    }
}
