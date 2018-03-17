package xmu.ackerman.thread;

import xmu.ackerman.context.Context;
import xmu.ackerman.context.HttpContext;
import xmu.ackerman.handler.HtmlHandler;
import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.ResponseService;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Ackerman
 * @Description: 一个接受请求, 并处理返回结果的工作线程
 * @Date: Created in 下午5:12 18-3-15
 */
public class HTTPThread implements Runnable {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    private Context context;

    private SelectionKey key;

    private RequestMessage requestMessage;

    private int number;
//    private ConcurrentHashMap<SelectionKey, Object> interestMap;

    public HTTPThread(RequestMessage requestMessage,
                      SelectionKey key
                     ){

        context = new HttpContext();
        this.requestMessage = requestMessage;
        this.key = key;
        number = atomicInteger.incrementAndGet();
//        this.interestMap = interestMap;
    }

    public String getThreadName(){
        return "HTTP thread: " + number;
    }

    /**
    * @Description: 线程并发处理请求
    * @Date: 下午3:32 18-3-16
    */
    public void run() {
//        System.out.println("Thread start: " + number);

        try {
            context.setContext(requestMessage, key);

            ResponseService.initResponse(context);

            HtmlHandler htmlHandler = context.getResponse().getHtmlHandler();

            htmlHandler.init(context);

            ResponseService.write(context);

//            key.interestOps(key.interestOps() | SelectionKey.OP_READ);

        }catch (Exception e){
//            System.out.println("HttpThread Exception" + e);
        }finally {
            try {
                //少了这行代码
                //压力测试不通过
                key.channel().close();
            }catch (Exception ee){

            }
        }

//        System.out.println("Thread end: " + number);

    }
}
