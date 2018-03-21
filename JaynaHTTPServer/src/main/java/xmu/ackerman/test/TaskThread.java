package xmu.ackerman.test;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午3:07 18-3-21
 */
public class TaskThread implements Runnable{

    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(Thread.currentThread().getName()+ " is done");
        }catch (Exception e){

        }
    }
}
