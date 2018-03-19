package xmu.ackerman.thread;

import xmu.ackerman.context.Context;
import xmu.ackerman.context.HttpContext;
import xmu.ackerman.context.HttpRequest;
import xmu.ackerman.handler.HtmlHandler;
import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.ResponseService;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Ackerman
 * @Description: 一个接受请求, 并处理返回结果的工作线程
 * @Date: Created in 下午5:12 18-3-15
 */
public class WriteThread implements Runnable {
    private static final long ALIVE_TIME = 200;

    private static AtomicInteger atomicInteger = new AtomicInteger();

    private Context context;

    private SelectionKey key;

    private RequestMessage requestMessage;

    private int number;

    private HttpRequest request;

    private boolean keepAlive;

    public WriteThread(HttpRequest request,
                       SelectionKey key,
                       boolean keepAlive){

        this.context = new HttpContext();
        this.requestMessage = request.getMessage();
        this.key = key;
        number = atomicInteger.incrementAndGet();
        this.request = request;
        this.keepAlive = keepAlive;
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

            resetHttpRequest();

        }catch (Exception e){
//            System.out.println("HttpThread Exception" + e);
        }finally {
            try {
                //少了这行代码
                //压力测试不通过
                //在keepALive模式下 要关闭, 关闭通道由priorityQueue执行
                if(!keepAlive) {
                    key.channel().close();
                }
            }catch (Exception ee){

            }
        }

    }


    public void resetHttpRequest(){
        request.setMessage(new RequestMessage());

        key.attach(request);
    }
}
