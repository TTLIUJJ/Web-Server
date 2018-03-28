package xmu.ackerman.service;


import java.nio.channels.SelectionKey;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午9:44 18-3-24
 */
public class MonitorService {

    private ConcurrentHashMap<SelectionKey, MonitoredKey> map;

    private PriorityBlockingQueue<MonitoredKey> queue;

    private ReentrantReadWriteLock lock;

    private long timeout;

    public MonitorService(long timeout){
        this.timeout = timeout;

        this.map = new ConcurrentHashMap<SelectionKey, MonitoredKey>(1024);
        this.queue = new PriorityBlockingQueue<MonitoredKey>(1024, new Comparator<MonitoredKey>() {
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
    public void putTask(SelectionKey key){
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
    public void updateTask(SelectionKey key){
        lock.readLock().lock();
        try{
            MonitoredKey monitoredKey = map.get(key);
            queue.remove(monitoredKey);

            monitoredKey.setExpireTime(new Date().getTime() + timeout);
            queue.add(monitoredKey);
        }catch (Exception e){
            System.out.println("updateTask: " + e);
        } finally{
            lock.readLock().unlock();
        }
    }

    /**
    * @Description: 获取写锁, 关闭过期的通道
    * @Date: 上午11:20 18-3-24
    */
    public void removeTask(){
        lock.writeLock().lock();
        try{
            long now = new Date().getTime();
            while(!queue.isEmpty()){
                MonitoredKey monitoredKey = queue.peek();
                if(now <= monitoredKey.getExpireTime()){
                    break;
                }
                try{
                    map.remove(monitoredKey.getKey());
                    queue.poll();
                }finally {
                    try{

                    }catch (Exception ee){
                        System.out.println("removeTask close Key: " + ee);
                    }finally {
                        monitoredKey.getKey().channel().close();
                    }
                }
            }
        }catch (Exception e){
            System.out.println("removeTask: " + e);
        }finally {
            lock.writeLock().unlock();
        }
    }

    private static class MonitoredKey {
        private SelectionKey key;

        private long expireTime;

        public MonitoredKey(SelectionKey key, long expireTime){
            this.key = key;
            this.expireTime = expireTime;
        }

        public SelectionKey getKey() {
            return key;
        }

        public void setKey(SelectionKey key) {
            this.key = key;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }
    }
}
