package xmu.ackerman.service;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Ackerman
 * @Description: 线程池饱和的拒绝策略
 * @Date: Created in 上午9:49 18-3-17
 */
public class RejectedStrategy implements RejectedExecutionHandler{
    public static AtomicInteger atomicInteger = new AtomicInteger();

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            System.out.println("拒绝任务计数: " + atomicInteger.incrementAndGet());
        }catch (Exception e){
//            System.out.println("拒绝策略异常" + e);
        }
    }
}
