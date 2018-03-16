package xmu.ackerman.work_thread;


import xmu.ackerman.HttpRequest;
import xmu.ackerman.zparse_http.HttpHeaderKV;
import xmu.ackerman.zparse_http.ParseRequestUtil;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

// 将解析后的http parse
// 分配合理的资源,  分配请求对应的资源
public class ReadThread implements Runnable{

    private Selector selector;
    private SelectionKey key;

    public ReadThread(Selector selector, SelectionKey key){
        this.selector = selector;
        this.key = key;
    }

    public void run() {
        SocketChannel client = null;
        try {
            System.out.println("read");

            client = (SocketChannel) key.channel();

//            HttpRequest request = new HttpRequest(key);
//            client.read(request.getBuff());
//            request.getBuff().flip();
//            String msg = Charset.forName("UTF-8").newDecoder().decode(request.getBuff()).toString();
//            System.out.println(msg);

            //测试 parseUtil
            HttpRequest request = new HttpRequest(key);
            ParseRequestUtil.doRequest(request);

            LinkedList<HttpHeaderKV> list = request.getHeaderList();
//            for(int i = 0; i < list.size(); ++i){
//                HttpHeaderKV kv = list.get(i);
//                System.out.println("key: " + kv.getHeaderKey() +", value: " + kv.getHeaderValue());
//            }


        }catch (Exception e){
            //TODO
            e.printStackTrace();
            System.err.println("read exception" + e);
        }
    }
}
