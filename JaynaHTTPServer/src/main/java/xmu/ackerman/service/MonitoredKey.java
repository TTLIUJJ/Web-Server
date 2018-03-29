package xmu.ackerman.service;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午8:32 18-3-28
 */
public class MonitoredKey {
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
