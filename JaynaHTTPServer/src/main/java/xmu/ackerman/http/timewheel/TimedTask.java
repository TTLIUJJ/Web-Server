package xmu.ackerman.http.timewheel;

import java.util.PriorityQueue;

/**
 * @Program: JaynaHTTPServer
 * @Description: 时间轮盘的封装的任务
 * @Author: Ackerman
 * @Create: 2019-03-04 15:41
 */
public class TimedTask {
    private long delayMs;
    private long expireTimestamp;
    private Runnable task;
    private volatile boolean cancel;
    private Bucket bucket;
    private long sequence;

    public TimedTask(long delayMs, Runnable task) {
        this.delayMs = delayMs;
        this.task = task;
        this.expireTimestamp = System.currentTimeMillis() + delayMs;
        this.cancel = false;
    }

    public void cancel() {
        this.cancel = true;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
