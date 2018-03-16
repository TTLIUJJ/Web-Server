package xmu.ackerman.zparse_http.header_kv_handler;

import xmu.ackerman.HttpRequest;
import xmu.ackerman.HttpResponse;
import xmu.ackerman.zparse_http.HttpHeaderKV;

import java.util.LinkedList;

public class HeaderKVHandlerUtil {
    private static final String PREFIX = "xmu.ackerman.zparse_http.header_kv_handler.";

    public static void parseKVHeader(HttpRequest request, HttpResponse response){
        LinkedList<HttpHeaderKV> list = request.getHeaderList();
        for(int i = 0; i < list.size(); ++i){
            HttpHeaderKV httpHeaderKV = list.get(i);
            String key = httpHeaderKV.getHeaderKey();
            try{
                Class c = Class.forName(PREFIX + key);
                HeaderKVHandler headerKVHandler = (HeaderKVHandler) c.newInstance();
                headerKVHandler.execute(httpHeaderKV, response);
            }catch (Exception e){
                //TODO
            }
        }

    }

}
