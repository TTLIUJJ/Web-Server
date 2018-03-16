package xmu.ackerman.controller;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Ackerman
 * @Description: 将获取到的key 保存到并发非阻塞队列
 * @Date: Created in 上午10:00 18-3-16
 */
public class InterestThread implements Runnable {

    private Selector selector;

    private LinkedBlockingQueue<SelectionKey> interestQueue;

    public InterestThread(Selector selector, LinkedBlockingQueue queue){
        this.selector = selector;
        this.interestQueue = queue;
    }

    public void run(){
        while (true){
            try{
                int readyKeys = selector.select();
                if(readyKeys == 0){ continue; }

                Set<SelectionKey> readySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readySet.iterator();

                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(!interestQueue.contains(key)) {
                        interestQueue.offer(key);
                    }
                }
            }catch (Exception e){
                //TODO
                System.out.println("workThread");
            }
        }
    }
}
