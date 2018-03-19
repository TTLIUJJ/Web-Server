package xmu.ackerman.thread;

import xmu.ackerman.context.HttpRequest;
import xmu.ackerman.service.RequestService;
import xmu.ackerman.utils.RequestParseState;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午9:24 18-3-18
 */
public class ReadThread implements Runnable {

    private Selector selector;

    private SelectionKey key;

    public ReadThread(Selector selector, SelectionKey key){
        this.selector = selector;
        this.key = key;
    }

    public void run(){
        try {

            SocketChannel client = (SocketChannel) key.channel();
            HttpRequest request = (HttpRequest) key.attachment();
            //接受的数据包有误, 不接受此次请求
            //并不是404错误, 而是发送不能识别或者错误的数据包
            RequestParseState state = RequestService.recvFrom(request, key);
            switch (state) {
                case PARSE_ERROR:
                    key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                    key.channel().close();
                    break;
                case PARSE_MORE:
                    client.register(selector, SelectionKey.OP_READ, request);
                    break;

                case PARSE_OK:
                    client.register(selector, SelectionKey.OP_WRITE);
                    key.attach(request);
                    // 没有这个  总是被卡住
                    selector.wakeup();

                    break;
            }
        }catch (Exception e){
//            System.out.println("ReadThread Exception: " + e);
        }
    }
}
