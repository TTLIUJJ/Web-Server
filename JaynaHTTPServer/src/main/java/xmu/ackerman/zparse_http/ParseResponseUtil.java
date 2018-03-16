package xmu.ackerman.zparse_http;

import xmu.ackerman.utils.FileType;
import xmu.ackerman.HttpResponse;
import xmu.ackerman.utils.FilePath;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;

public class ParseResponseUtil {
    //目前假设一次就可以全部写入完毕
    public static void doResponse(HttpResponse response){
        // 1 包装请求头
        //       1 error
        //       2 normal
        // 2 返回请求和资源文件 根据resourceFielPath 获取
        try {
            if(response == null){
                return;
            }
            SelectionKey key = response.getKey();
            SocketChannel client = (SocketChannel) key.channel();

//            ByteBuffer buffer = getByteBuffer(response.getResponseFile());
//
//            client.write(ByteBuffer.wrap(aHeader(buffer.capacity()).getBytes()));
//            client.write(buffer);
            response.setKeepAlive(true);
            response.setModified(true);
            serveStatic(response);

            client.write(response.getHeaderBuffer());
            client.write(response.getResourceBuffer());

            key.interestOps(SelectionKey.OP_READ);
        }catch (Exception e){
            //TODO
            System.out.print(e);
        }
    }


//    private static void process404(HttpResponse response){
//        try{
//            SelectionKey key = response.getSelectionKey();
//            response.setResponseFile("/error/error_404.html");
//            key.attach(response);
//            key.interestOps(SelectionKey.OP_WRITE);
//        }catch (Exception e){
//            //TODO
//        }
//    }

    private static ResponseCode getResponseCode(HttpResponse response){
        ResponseCode code ;
        switch (response.getStatus()){
            case 200:
                code = ResponseCode.OK;
                break;

            case 404:
                code = ResponseCode.NOT_FOUND;
                break;

            default:
                code = ResponseCode.INTERNAL_SERVER_ERROR;
        }

        return code;
    }

    private static ByteBuffer getByteBuffer(String resourceFile){
        try {

            File file = new File(resourceFile);
            InputStream inputStream = new FileInputStream(file);
            byte []bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            return ByteBuffer.wrap(bytes);
        }catch (Exception e){
            //TODO
        }

        return null;
    }


    private static void serveStatic(HttpResponse response){
        try {
            ResponseCode code = getResponseCode(response);
            int status = code.getStatus();
            String sourceFile;
            String header;
            ByteBuffer resourceBuffer;

            if (status == 200) {
                sourceFile = response.getResponseFile();
                resourceBuffer = getByteBuffer(sourceFile);
                response.setFileSize(resourceBuffer.capacity());

                header = getNormalHeader(response);
            } else {
                FilePath filePath = FilePath.getResourcesFile();
                String filename = code.getFilename();
                sourceFile = filePath.getFilePath(filename);
                resourceBuffer = getByteBuffer(sourceFile);
                response.setFileSize(resourceBuffer.capacity());

                header = getErrorHeader(response, code);
            }
            ByteBuffer headerBuffer = ByteBuffer.wrap(header.getBytes());
            response.setHeaderBuffer(headerBuffer);
            response.setResourceBuffer(resourceBuffer);

        }catch (Exception e){
            //TODO
        }
    }

    private static String getNormalHeader(HttpResponse response){
        StringBuilder header = new StringBuilder();
        ResponseCode code = ResponseCode.OK;
        header.append("HTTP/1.1 " + code.getStatus() +" " + code.getMsg() + "\r\n");
        if(response.isKeepAlive()){
            header.append("Connection: keep-alive\r\n");
            header.append("Keep-Alive: timeout=50\r\n");    // 50ms
        }

        if(response.isModified()){
            String mimeType = parseMimeType(response);
            int fileSize = response.getFileSize();
            Date date = new Date();
            header.append("Content-type: " + mimeType + "\r\n");
            header.append("Content-length: " + fileSize + "\r\n");
            header.append("Last-Modified: " + date + "\r\n");
        }

        header.append("Server: Jayna\r\n\r\n");

        return header.toString();
    }

    private static String getErrorHeader(HttpResponse response, ResponseCode code){
        StringBuilder header = new StringBuilder();
        Date now = new Date();
        header.append("HTTP/1.1 " + code.getStatus() + " " + code.getMsg() + "\r\n");
        header.append("Date: " + now + "\r\n");
        header.append("Server: Jayna\r\n");
        header.append("Content-type: text/html\r\n");
        header.append("Connection: close\r\n");
        header.append("Content-length: " + response.getFileSize() + "\r\n\r\n");

        response.setFileName(code.getFilename());

        return header.toString();
    }

    private static String parseMimeType(HttpResponse response){
        String defaultType = "text/plain";

        String filename = response.getFileName();
        int length = filename.length();
        int lastDot = length-1;

        char ch;
        while(lastDot >= 0){
            ch = filename.charAt(lastDot);
            if(ch == '.'){
                break;
            }
            --lastDot;
        }
        if(lastDot < 0){
            return defaultType;
        }
        String suffix = filename.substring(lastDot, length);
        FileType fileType = FileType.getFileType();
        return fileType.getContentTypeOrDefault(suffix, defaultType);
    }



    public static String aHeader(int len){
        Date now = new Date();
        String header = "HTTP/1.5 501 Not Implemented\r\n" +
                "Date: " + now + "\r\n" +
                "Server: SHIT 2.0\r\n" +
                "Content-length: " + len +"\r\n" +
                "Content-type: text/html" + "\r\n\r\n";

        return header;
    }

}
