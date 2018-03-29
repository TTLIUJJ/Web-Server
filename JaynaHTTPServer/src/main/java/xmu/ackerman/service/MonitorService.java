package xmu.ackerman.service;

import xmu.ackerman.thread.CancelTask;

import java.nio.channels.SelectionKey;
import java.util.*;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午9:44 18-3-24
 */
public class MonitorService {

//    private ConcurrentHashMap<SelectionKey, MonitoredKey> map;
    private HashMap<SelectionKey, MonitoredKey> map;

//    private PriorityBlockingQueue<MonitoredKey> queue;
    private PriorityQueue<MonitoredKey> queue;

    private ReentrantReadWriteLock lock;

    private long timeout;

    private ConcurrentHashMap<SelectionKey, FutureTask<Void>> futureTaskMap = new ConcurrentHashMap<SelectionKey, FutureTask<Void>>(1024);

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    public MonitorService(long timeout){
        this.timeout = timeout;

//        this.map = new ConcurrentHashMap<SelectionKey, MonitoredKey>(1024);
//        this.queue = new PriorityBlockingQueue<MonitoredKey>(1024, new Comparator<MonitoredKey>() {
//            public int compare(MonitoredKey o1, MonitoredKey o2) {
//                return (int)(o1.getExpireTime() - o2.getExpireTime());
//            }
//        });
        this.map = new HashMap<SelectionKey, MonitoredKey>(1024);
        this.queue = new PriorityQueue<MonitoredKey>(1024, new Comparator<MonitoredKey>() {
            public int compare(MonitoredKey o1, MonitoredKey o2) {
                return (int)(o1.getExpireTime() - o2.getExpireTime());
            }
        });
        this.lock = new ReentrantReadWriteLock();

    }

    /**
    * @Description: 新增一个定时任务, 添加的新任务不必加锁, 添加的时候肯定不会过期
    * @Date: 上午9:50 18-3-24
    */
    public synchronized void putTask(SelectionKey key){
//        lock.readLock().lock();
        try {
            long expireTime = new Date().getTime() + timeout;
            MonitoredKey monitoredKey = new MonitoredKey(key, expireTime);
            queue.add(monitoredKey);
            map.put(key, monitoredKey);

        }catch (Exception e){
            System.out.println("putTask: " + e);
        }finally {
//            lock.readLock().unlock();
        }
    }

    /**
    * @Description: 多线程获取读锁, 每个线程更新各自通道
    * @Date: 上午11:20 18-3-24
    */
    public synchronized void updateTask(SelectionKey key){
//        lock.writeLock().lock();
        try{
            MonitoredKey monitoredKey = map.get(key);
            queue.remove(monitoredKey);

            monitoredKey.setExpireTime(new Date().getTime() + timeout);
            queue.add(monitoredKey);


        }catch (Exception e){
            System.out.println("updateTask: " + e);
        } finally{
//            lock.writeLock().unlock();
        }
    }

    /**
    * @Description: 获取写锁, 关闭过期的通道
    * @Date: 上午11:20 18-3-24
    */
    public synchronized void removeTask(){
//        lock.writeLock().lock();
        try{
            while(!queue.isEmpty()){
                long now = new Date().getTime();
                MonitoredKey monitoredKey = queue.peek();
                if(now <= monitoredKey.getExpireTime()){
                    break;
                }
                try{
                    map.remove(monitoredKey.getKey());
                    queue.remove();
                }finally {
                    monitoredKey.getKey().channel().close();
                }
            }
        }catch (Exception e){
            System.out.println("removeTask: " + e);
        }finally {
//            lock.writeLock().unlock();
        }
    }



    public void addFutureTask(SelectionKey selectionKey){
        Callable<Void> c = new CancelTask(selectionKey);
        FutureTask<Void> futureTask = new FutureTask<Void>(c);  //任务是简单关闭Socket连接
        futureTaskMap.put(selectionKey, futureTask);    //futureTaskMap是ConcurrentHashMap类
        scheduledThreadPoolExecutor.schedule(futureTask, timeout, TimeUnit.MILLISECONDS);   //timeout是HTTP连接时长
    }

    public void updateFutureTask(SelectionKey selectionKey){
        FutureTask futureTask = futureTaskMap.get(selectionKey);
        if(futureTask.cancel(false)){   //取消当前任务, 更新定时任务的时间
            addFutureTask(selectionKey);
        }
    }

    public void removeFutureTaskAndCloseSocket(SelectionKey selectionKey){
        FutureTask futureTask = futureTaskMap.get(selectionKey);
        if(futureTask.cancel(false)){
            futureTaskMap.remove(selectionKey);
            return;
        }
        try {
            selectionKey.channel().close();
        }catch (Exception e){
            System.out.println("removeFutureTaskAndCloseSocket: " + e);
        }
    }

    public static void main(String []args){
        ArrayList arrayList = new ArrayList();
        arrayList.add(1);
        arrayList.add(2);
        List b = arrayList.subList(0,1);
        System.out.println(arrayList.size());
    }
}
