package xmu.practice.chargen;

import java.util.Date;

public class Util {

    public static String response(){
        String body= "<HTML>\r\n" +
                "<HEAD><TITLE>title</TITLE></HEAD>\r\n" +
                "<BODY>\r\n" +
                "<H1>HTTP RESPONSE</H1>\r\n" +
                "</BODY>\r\n" +
                "</HTML>";

        Date now = new Date();
        String header = "HTTP/1.5 501 Not Implemented\r\n" +
                "Date: " + now + "\r\n" +
                "Server: SHIT 2.0\r\n" +
                "Content-length: " + body.length() +"\r\n" +
                "Content-type: text/html" + "\r\n\r\n";

        return header+body;
    }



    public static void main(String []args){

    }
}
