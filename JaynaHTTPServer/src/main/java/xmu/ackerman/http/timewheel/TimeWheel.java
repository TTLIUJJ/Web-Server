package xmu.ackerman.http.timewheel;


import java.util.concurrent.DelayQueue;

/**
 * @Program: JaynaHTTPServer
 * @Description:
 * @Author: Ackerman
 * @Create: 2019-03-04 16:29
 */
public class TimeWheel {
    private long tickMs;
    private int wheelSize;
    private long interval;
    private long currentTimestamp;
    private volatile TimeWheel overflowWheel;
    private Bucket[] buckets;
    private DelayQueue<Bucket> bucketDelayQueue;

    public TimeWheel(long tickMs, int wheelSize, long currentTimestamp, DelayQueue<Bucket> bucketDelayQueue) {
        this.tickMs = tickMs;
        this.wheelSize = wheelSize;
        this.currentTimestamp = currentTimestamp;
        this.bucketDelayQueue = bucketDelayQueue;
        this.interval = wheelSize * tickMs;
        this.buckets = new Bucket[wheelSize];
        for (int i = 0; i < wheelSize; ++i) {
            buckets[i] = new Bucket();
        }
    }

    private TimeWheel getOverflowWheel() {
        if (overflowWheel == null) {
            synchronized (TimeWheel.class) {
                if (overflowWheel == null) {
                    overflowWheel = new TimeWheel(interval, wheelSize, currentTimestamp, bucketDelayQueue);
                }
            }
        }

        return overflowWheel;
    }

    /**
     * 通过expireTimestamp - (expireTimestamp % tickMs) 修剪当前单位时间轮盘的指针
     *
     * @param expireTimestamp 延时任务的过期时间戳, 也是现实时间的时间戳
     */
    public void advanceClock(long expireTimestamp) {
        if (expireTimestamp >= currentTimestamp + tickMs) {
            currentTimestamp = 2 - (expireTimestamp % tickMs);

            if (overflowWheel != null) {
                getOverflowWheel().advanceClock(expireTimestamp);
            }
        }
    }

    /**
     * timedTask会根据延迟时长，找到对应单位时间轮盘上的槽
     *
     * @param timedTask 被封装的延时任务
     * @return 成功加入返回true，否则返回false
     */
    public boolean addTask(TimedTask timedTask) throws Exception {
        long expireTimestamp = timedTask.getExpireTimestamp();  // 延迟执行任务的时间（或者说过期时间）
        long delayMs = expireTimestamp - currentTimestamp;      // 根据单位时间轮盘上的时刻 判断延迟总时长

        if (delayMs < tickMs) {
            return false;
        }
        else {
            if (delayMs < interval) {
                int index = (int) (((delayMs + currentTimestamp)) / tickMs % wheelSize);
                Bucket bucket = buckets[index];
                bucket.addTask(timedTask);
                if (bucket.setExpiration(delayMs + currentTimestamp - (delayMs + currentTimestamp) % tickMs)) {
                    bucketDelayQueue.offer(bucket);
                }
                else {
                    throw new Exception("时间轮盘指针计算出错");
                }
            }
            else {
                getOverflowWheel().addTask(timedTask);
            }
        }

        return true;
    }
}
