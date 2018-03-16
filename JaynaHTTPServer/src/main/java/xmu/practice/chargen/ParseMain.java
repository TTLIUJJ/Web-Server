package xmu.practice.chargen;

import xmu.ackerman.utils.FileType;

public class ParseMain {

    public static String parseURI(JaynaHttpRequest request){
        String filename = request.getRoot();

        int uriBeg = request.getUriStart();
        int uriEnd = request.getUriEnd();
        int qMark = uriBeg;
        char []buff = request.getBuff();

        // 找到'?'位置界定非参部分
        while(qMark < uriEnd && buff[qMark] != '?'){ ++ qMark; }

        // 将uri中属于'?'之前部分内容追加到filename
        filename += new String(buff, uriBeg, qMark);

        // 在请求中找到最后一个'/'位置界定文件位置
        int lastSlash = filename.length()-1;
        while(lastSlash >= 0 && filename.charAt(lastSlash) != '/') { -- lastSlash; }

        // 在文件名中找到最后一个'.'界定文件类型
        int lastDot = filename.length() - 1;
        while(lastDot >= 0 && filename.charAt(lastDot) != '.') { -- lastDot; }

        // 请求文件时末尾加'/'
        if(lastDot == -1 && filename.charAt(filename.length()-1) != '/'){
            filename += "/";
        }

        // 默认请求index.html
        if(filename.charAt(filename.length()-1) == '/'){
            filename += "index.html";
        }

        return filename;
    }


    public static String getFileType(String suffix){
        FileType fileType = FileType.getFileType();
        return fileType.getContentTypeOrDefault(suffix, "text/plain");
    }



    public void doRequest(JaynaHttpRequest request){

    }
}
