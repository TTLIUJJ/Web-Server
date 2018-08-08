package xmu.ackerman.http.thread;

import xmu.ackerman.JaynaHttpController;
import xmu.ackerman.http.context.Context;
import xmu.ackerman.http.service.RequestService;
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

    private SelectionKey selectionKey;

    private Context context;

    public ReadThread(Context context){
        this.context = context;
        this.selector = context.getSelector();
        this.selectionKey = context.getSelectionKey();
    }

    public void run(){
        try {

            //接受的数据包有误, 不接受此次请求
            //并不是404错误, 而是发送不能识别或者错误的数据包
            SocketChannel client = (SocketChannel) selectionKey.channel();
            RequestParseState state = RequestService.recvFrom(context);

            switch (state) {
                case PARSE_ERROR:
                    context = null;
                    JaynaHttpController.monitorService.removeFutureTaskAndCloseSocket(selectionKey);
                    break;
                case PARSE_MORE:
                    client.register(selector, SelectionKey.OP_READ, context);
                    break;

                case PARSE_OK:
                    client.register(selector, SelectionKey.OP_WRITE, context);
                    // 没有这个  总是被卡住
                    selector.wakeup();

                    break;
            }
        }catch (Exception e){
            System.out.println("ReadThread Exception: " + e);
        }
    }
}
