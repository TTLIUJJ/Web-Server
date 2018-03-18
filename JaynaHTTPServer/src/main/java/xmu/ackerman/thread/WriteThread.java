package xmu.ackerman.thread;

import xmu.ackerman.context.Context;
import xmu.ackerman.context.HttpContext;
import xmu.ackerman.context.HttpRequest;
import xmu.ackerman.handler.HtmlHandler;
import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.ResponseService;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
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

//    private ArrayBlockingQueue<SelectionKey> queue;
//    private LinkedBlockingQueue<SelectionKey> queue;
//
//    private ConcurrentHashMap<SelectionKey, Long> map;

    private LinkedList<SelectionKey> queue;
    private HashMap<SelectionKey, Long> map;

//    private ConcurrentHashMap<SelectionKey, Object> interestMap;

    public WriteThread(HttpRequest request,
                       SelectionKey key,
//                       ArrayBlockingQueue<SelectionKey> queue,
//                       LinkedBlockingQueue<SelectionKey> queue,
//                       ConcurrentHashMap<SelectionKey, Long> map
                       LinkedList<SelectionKey> queue,
                       HashMap<SelectionKey, Long> map
                         ){

        this.context = new HttpContext();
        this.requestMessage = request.getMessage();
        this.key = key;
        number = atomicInteger.incrementAndGet();
        this.request = request;
        this.queue = queue;
        this.map = map;
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

            if(updateExpireTime()){
                //成功入队
                //reset请求, 资源复用
                resetHttpRequest();
            }
            else{
                key.channel().close();
            }


        }catch (Exception e){
//            System.out.println("HttpThread Exception" + e);
        }finally {
            try {
                //少了这行代码
                //压力测试不通过
//                key.channel().close();
            }catch (Exception ee){

            }
        }

//        System.out.println("Thread end: " + number);

    }

    /**
    * @Description: 设置或者更新过期时间
    * @Date: 下午3:00 18-3-18
    */
    private boolean updateExpireTime(){
        try{
            //更新
            long expireTime = new Date().getTime() + ALIVE_TIME;
            if(map.containsKey(key)){

                //先删除旧的, 在加入新的
                //顺序就会调整
                //队列有限, 入队成功才能加入map
                if(queue.remove(key) && queue.offer(key)){
                    map.put(key, expireTime);
                    return true;
                }
            }
            else{
                //首次入队成功
                if(queue.offer(key)){
                    map.put(key, expireTime);
                    return true;
                }
            }
        }catch (Exception e){
            System.out.println("WriteThread: " + e);
        }
        return false;
    }

    public void resetHttpRequest(){
        request.setMessage(new RequestMessage());
    }
}
