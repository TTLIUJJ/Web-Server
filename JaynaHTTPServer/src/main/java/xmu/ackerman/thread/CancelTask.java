package xmu.ackerman.thread;

import java.nio.channels.SelectionKey;
import java.util.concurrent.Callable;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午11:10 18-3-28
 */
public class CancelTask implements Callable<Void> {

    private SelectionKey key;

    public CancelTask(SelectionKey key){
        this.key = key;
    }

    public Void call(){
        try{
            key.channel().close();
        }catch (Exception e){
            System.out.println("call: " + e);
        }
        return null;
    }
}
