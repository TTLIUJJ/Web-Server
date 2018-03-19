package xmu.ackerman.service;

import java.nio.channels.SelectionKey;
import java.util.Date;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午12:02 18-3-19
 */
public class MonitoredKey {
    public static final int DEFAULT_ALIVE_TIME = 200;    // ms

    private SelectionKey key;

    private long expireTime;

    public MonitoredKey(SelectionKey key){
        this.key = key;
        Date time = new Date();
        expireTime = time.getTime() + DEFAULT_ALIVE_TIME;
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
