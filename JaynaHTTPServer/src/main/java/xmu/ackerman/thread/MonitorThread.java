package xmu.ackerman.thread;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午7:25 18-3-21
 */
public class MonitorThread implements Runnable {

    private SelectionKey key;

    public MonitorThread(SelectionKey key){
        this.key = key;
    }

    public void run(){
        try{
            key.channel().close();
        }catch (Exception e){
            System.out.println("MonitorThread Exception: " + e);
        }
    }
}
