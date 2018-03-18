package xmu.ackerman.thread;

import org.omg.CORBA.TIMEOUT;
import xmu.ackerman.test.C;
import xmu.ackerman.test.TestThread;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * @Author: Ackerman
 * @Description: KeepAlive 过期的key会被删除
 * @Date: Created in 下午2:02 18-3-18
 */
public class TimeMonitorThread implements Runnable {

//    private ArrayBlockingQueue<SelectionKey> queue;
//    private LinkedBlockingQueue<SelectionKey> queue;
//
//    private ConcurrentHashMap<SelectionKey, Long> map;

    private LinkedList<SelectionKey> queue;
    private HashMap<SelectionKey, Long> map;
    public TimeMonitorThread(
//            ArrayBlockingQueue<SelectionKey> queue,
//            LinkedBlockingQueue<SelectionKey> queue,
//            ConcurrentHashMap<SelectionKey, Long> map
            LinkedList<SelectionKey> queue,
            HashMap<SelectionKey, Long> map
        ){
        this.queue = queue;
        this.map = map;
    }

    public void run(){
        while (true){

            try{
                //阻塞 避免CPU空载
//                SelectionKey key = queue.take();
                SelectionKey key = queue.remove();
                if(key == null){
                    continue;
                }
                long expire = map.get(key);
                long now = new Date().getTime();
                long remain = expire - now;
                if(remain <= 0){
                    //关闭过期的通道
                    key.channel().close();
                }
                else{
                    //最旧的还未过期, 那么剩下的必然还未过期
                    //程序直接进入休眠, 直到过期
                    // 休眠之后, 再次判断, 防止在监控休眠期间, 通道再次被激活

                    //这不是一个最优的策略
                    //因为在执行完比较, 进入body之中,
                    //key还是有可能被激活的,
                    //其他的线程对key进行通信, 会异常
                    TimeUnit.MILLISECONDS.sleep(remain);
                    if(map.get(key) < remain + now){
                        queue.remove(key);
                        map.remove(key);
                        key.channel().close();
                    }
                }
            }catch (Exception e){
//                System.out.println("TimeMonitorThread: " + e);
            }

        }
    }





}
