package xmu.ackerman.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午3:05 18-3-21
 */
public class TestPool {
    public static void main(String []args){
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,
                20,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10)
        );
        for(int i = 0; i < 20; ++i){
            TaskThread taskThread = new TaskThread();
            Runnable r = new Thread(taskThread, "" + i+1);
            pool.execute(r);
        }
        try {

            TimeUnit.SECONDS.sleep(60);

            System.out.println("线程曾经创建的最大线程数： " + pool.getLargestPoolSize());
            System.out.println("线程目前还活跃的线程数： " + pool.getActiveCount());
        }catch (Exception e){

        }
    }
}
