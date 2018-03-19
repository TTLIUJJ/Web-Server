package xmu.ackerman.service;

import xmu.ackerman.context.Context;
import xmu.ackerman.context.Request;
import xmu.ackerman.context.Response;
import xmu.ackerman.handler.HandlerAdapter;
import xmu.ackerman.handler.HtmlHandler;
import xmu.ackerman.handler.entityHandler.error.Error404Handler;
import xmu.ackerman.utils.FilePath;
import xmu.ackerman.utils.FileType;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:52 18-3-16
 */
public class ResponseService {

    /**
    * @Description: 初始化 Response 需要的一些属性值
     *              1.资源的绝对路径 2.返回类型 3.返回状态码 4.状态码对应的msg
    * @Date: 上午11:11 18-3-16
    */
    public static void initResponse(Context context){
        Request request = context.getRequest();
        Response response = context.getResponse();

        String filename = request.getUri();

        String path = ResponseService.getFilePath(filename);
        response.setFilePath(path);

        int code = ResponseService.getStatusCode(context);
        response.setStatusCode(code);

        String msg = ResponseService.getStatusMsg(code);
        response.setStatusMsg(msg);

        //找不到资源, 返回的error_404页面
        //可以保证, 找到 filename(uri) --> path --> handler
        HtmlHandler htmlHandler;
        if(path == null){
            htmlHandler = new Error404Handler();
            //重置filename
            // 考虑到错误的404请求
            filename = htmlHandler.getFilename();
            path = resetFilePath(filename);
            response.setFilePath(path);
        }
        else{
            htmlHandler = ResponseService.getHtmlHandler(filename);
        }
        response.setHandler(htmlHandler);

        String contentType = ResponseService.getContentType(filename);
        response.setContentType(contentType);

    }


    public static String getFilePath(String filename){
        FilePath filePath = FilePath.getResourcesFile();
        return filePath.getFilePath(filename);
    }

    public static String getContentType(String filename){
        FileType fileType = FileType.getFileType();
        return fileType.getContentTypeOrDefault(filename, "text/plain");
    }

    public static int getStatusCode(Context context){
        // 找不到资源
        String path = context.getResponse().getFilePath();
        if(path == null){
            return 404;
        }
        //比如301, 403啊

        context.getResponse().setException(false);
        return 200;
    }

    public static String getStatusMsg(int code){
        String msg = "OK";
        switch (code){
            case 404:
                msg = "404 Can't find the resource";
                break;
            case 403:
                msg = "403 Resource forbidden";
                break;
            default:
                break;
        }

        return msg;
    }

    public static HtmlHandler getHtmlHandler(String filename){
        HandlerAdapter adapter = HandlerAdapter.getHandlerAdapter();
        return adapter.getEntityHandler(filename);
    }

    /**
    * @Description: 写回客户端
    * @Date: 下午3:54 18-3-16
    */
    public static void write(Context context){
        Response response = context.getResponse();
        String message = getResponseMessage(response);
        SocketChannel client;
        try{
            SelectionKey key = response.getSelectionKey();
            client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            client.write(buffer);

        }catch (Exception e){
            //TODO
//            System.out.println("write: " + e);
        }
    }

    /**
    * @Description: 获取渲染的页面以及响应头
    * @Date: 下午3:54 18-3-16
    */
    private static String getResponseMessage(Response response){
        String webContent = getWebContent(response);
        String header = getResponseHeader(response);

        return header + webContent;
    }

    /**
    * @Description: 获取渲染页面的资源和大小
    * @Date: 下午4:22 18-3-16
    */
    private static String getWebContent(Response response){
        String content = "";
        try{
            File file = new File(response.getFilePath());
            InputStream inputStream = new FileInputStream(file);
            byte []bytes = new byte[1024];
            int cnt;
            while((cnt = inputStream.read(bytes)) != -1) {
                content += new String(bytes, 0, cnt);
            }
            response.setContentLength(content.length());
        }catch (Exception e){
            //TODO
        }

        return content;
    }

    private static String getResponseHeader(Response response){
        if(response.isException()){
            return getExceptionHeader(response);
        }
        return getNormalHeader(response);
    }

    private static String getExceptionHeader(Response response){
        StringBuilder header = new StringBuilder();
        Date now = new Date();

        header.append("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMsg() + "\r\n");
        header.append("Date: " + now + "\r\n");
        header.append("Server: " + Response.SERVER_NAME + "\r\n");
        header.append("Content-type: text/html\r\n");
        header.append("Connection: close\r\n");
        header.append("Content-length: " + response.getContentLength() + "\r\n\r\n");

        return header.toString();

    }

    private static String getNormalHeader(Response response){
        StringBuilder header = new StringBuilder();
        Date now = new Date();

        header.append("HTTP/1.1 " + response.getStatusCode() + " " + response.getStatusMsg() + "\r\n");
        header.append("Connection: keep-alive\r\n");
        header.append("Keep-Alive: timeout=50\r\n");    // 50ms
        header.append("Content-type: " + response.getContentType() + "\r\n");
        header.append("Content-length: " + response.getContentLength() + "\r\n");
        header.append("Last-Modified: " + now + "\r\n");
        header.append("Server: "+ Response.SERVER_NAME + "\r\n\r\n");

        return header.toString();

    }

    private static String resetFilePath(String filename){
        FilePath filePath = FilePath.getResourcesFile();
        return filePath.getFilePath(filename);
    }

    public static void main(String []args){
        String path = "/home/ackerman/github/jaynaHttpServer/JaynaHTTPServer/src/main/resources/error/error_404.html";
        File file = new File(path);

        try{
            String content = "";
            InputStream inputStream = new FileInputStream(file);
            byte []bytes = new byte[1024];
            int cnt;
            while((cnt = inputStream.read(bytes)) != -1) {
                content += new String(bytes, 0, cnt);
            }
            System.out.println(content);
        }catch (Exception e){

        }
    }

}
