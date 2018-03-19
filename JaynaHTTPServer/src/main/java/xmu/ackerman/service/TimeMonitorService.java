package xmu.ackerman.service;

import java.nio.channels.SelectionKey;
import java.sql.Time;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午12:02 18-3-19
 */
public class TimeMonitorService {

    private PriorityBlockingQueue<MonitoredKey> queue;

    private Comparator<MonitoredKey> keyComparator = new Comparator<MonitoredKey>() {
        public int compare(MonitoredKey o1, MonitoredKey o2) {
            return (int)(o1.getExpireTime() - o2.getExpireTime());
        }
    };

    public TimeMonitorService(){
        queue = new PriorityBlockingQueue<MonitoredKey>(1000, keyComparator);
    }


    public void checkExpiredKey(){
        try {

            long now = new Date().getTime();
            while (!queue.isEmpty()) {
                //这个程序会删除大量kye, peek()操作增加工作量
                MonitoredKey monitoredKey = queue.remove();
                if (now > monitoredKey.getExpireTime()) {
                    try {
                        monitoredKey.getKey().channel().close();
                    } catch (Exception e) {
                        System.out.println("checkExpiredKey key.channel.close " + e);
                    }
                    continue;
                }
                //次key不是过期的 重新入队
                queue.add(monitoredKey);
                break;
            }
        }catch (Exception e){
            System.out.println("checkExpiredKey " + e);
        }
    }

    public void addMonitorKey(SelectionKey key){
        try {
            MonitoredKey monitoredKey = new MonitoredKey(key);
            queue.add(monitoredKey);
        }catch (Exception e){
            System.out.println("addMonitorKey " + e);
        }
    }
}