package xmu.ackerman.thread;

import xmu.ackerman.JaynaHttpController;
import xmu.ackerman.context.Context;
import xmu.ackerman.handler.HtmlHandler;
import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.ResponseService;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @Author: Ackerman
 * @Description: 一个接受请求, 并处理返回结果的工作线程
 * @Date: Created in 下午5:12 18-3-15
 */
public class WriteThread implements Runnable {

    private Context context;

    private Selector selector;

    private SelectionKey key;

    public WriteThread(Context context){
        this.context = context;
        this.selector = context.getSelector();
        this.key = context.getSelectionKey();
    }


    /**
    * @Description: 线程并发处理请求
    * @Date: 下午3:32 18-3-16
    */
    public void run() {
        try {
            ResponseService.initResponse(context);

            HtmlHandler htmlHandler = context.getResponse().getHtmlHandler();

            htmlHandler.init(context);

            ResponseService.write(context);


        }catch (Exception e){
            System.out.println("WriteThread Exception" + e);
        }finally {
            try {

                if(JaynaHttpController.keepAlive) {
                    //保持连接状态, 通道注册读事件
                    resetHttpRequest();
                    SocketChannel client = (SocketChannel) key.channel();
                    client.register(selector, SelectionKey.OP_READ, context);
                    JaynaHttpController.monitorService.updateFutureTask(key);

                }
                else{
                    //立即关闭通道
                    key.channel().close();
                }
            }catch (Exception ee){
                System.out.println("WriteThread finally: " + ee);
            }
        }

    }


    public void resetHttpRequest(){
        this.context.getRequest().setRequestMessage(new RequestMessage());
    }
}
