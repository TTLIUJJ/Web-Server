package xmu.ackerman.service;

import java.nio.channels.SelectionKey;
import java.sql.Time;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午12:02 18-3-19
 */
public class TimeMonitorService {

//    private PriorityBlockingQueue<MonitoredKey> queue;

    private ConcurrentLinkedQueue<MonitoredKey> queue;

//    private Comparator<MonitoredKey> keyComparator = new Comparator<MonitoredKey>() {
//        public int compare(MonitoredKey o1, MonitoredKey o2) {
//            return (int)(o1.getExpireTime() - o2.getExpireTime());
//        }
//    };

    public TimeMonitorService(){
//        queue = new PriorityBlockingQueue<MonitoredKey>(1000, keyComparator);
        queue = new ConcurrentLinkedQueue<MonitoredKey>();
    }


    public void checkExpiredKey(){
        try {

            long now = new Date().getTime();
            MonitoredKey monitoredKey;
            while((monitoredKey = queue.peek()) != null){
                long expireTime = monitoredKey.getExpireTime();
                if(now >= expireTime){
                    monitoredKey.getKey().channel().close();
                    queue.poll();
                }
                else{
                    break;
                }
            }

        }catch (Exception e){
            System.out.println("checkExpiredKey " + e);
        }
    }

    public void addMonitorKey(SelectionKey key){
        try {
            MonitoredKey monitoredKey = new MonitoredKey(key);
            queue.offer(monitoredKey);
        }catch (Exception e){
            System.out.println("addMonitorKey " + e);
        }
    }
}