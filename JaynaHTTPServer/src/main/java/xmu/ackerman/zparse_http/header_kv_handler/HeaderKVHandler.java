package xmu.ackerman.zparse_http.header_kv_handler;

import xmu.ackerman.HttpResponse;
import xmu.ackerman.zparse_http.HttpHeaderKV;

public interface HeaderKVHandler {
    void execute(HttpHeaderKV httpHeaderKV, HttpResponse response);
}
