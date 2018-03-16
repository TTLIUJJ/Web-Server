package xmu.ackerman.zparse_http.header_kv_handler;

import xmu.ackerman.HttpResponse;
import xmu.ackerman.zparse_http.HttpHeaderKV;

public class ConnectionHeaderKVHandler implements HeaderKVHandler{
    public void execute(HttpHeaderKV kv, HttpResponse response){
        if(kv.getHeaderValue().equals("keep-alive")){
            response.setKeepAlive(true);
        }
    }
}
