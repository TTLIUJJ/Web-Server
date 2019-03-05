package xmu.ackerman.http.timewheel;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Program: JaynaHTTPServer
 * @Description:
 * @Author: Ackerman
 * @Create: 2019-03-04 16:49
 */
public class Timer {
    private long tickMs;
    private TimeWheel timeWheel;
    private DelayQueue<Bucket> bucketDelayQueue = new DelayQueue<>();
    private ExecutorService monitorThreadPool;
    private ExecutorService workThreadPool;

    private AtomicLong consumerCounter = new AtomicLong(0);
    private AtomicLong rejectCounter   = new AtomicLong(0);

    public Timer(long tickMs, int wheelSize) {
        this.tickMs = tickMs;
        monitorThreadPool = Executors.newSingleThreadExecutor();
        workThreadPool = new ThreadPoolExecutor(4, 8, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        rejectCounter.incrementAndGet();
                    }
                });
        this.timeWheel = new TimeWheel(tickMs, wheelSize, System.currentTimeMillis(), bucketDelayQueue);
    }

    /**
     * 如果加入的延时任务小于最小单位时间轮盘的单位刻度，那么任务直接加入线程池
     * 否则，延时任务会根据其延时时长加入至对应单位时间轮盘的槽中
     *
     * @param timedTask 被封装的延时任务
     */
    public void addTask(TimedTask timedTask) {
        try {
            if (!timeWheel.addTask(timedTask)) {
                if (!timedTask.isCancel()) {
                    workThreadPool.execute(timedTask.getTask());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTask(TimedTask timedTask) {
        assert (!timedTask.isCancel());


    }

    public void update(TimedTask timedTask) {

    }

    public void start(long timeout, TimeUnit unit) {
        monitorThreadPool.execute(() -> {
            while (true) {
                advanceClock(timeout, unit);
            }
        });
    }

    /**
     * poll最多等待timeout时间
     * 如果有过期任务，返回延时任务对应的槽
     * 如果没有任务，  返回null
     *
     * @param timeout 最多等待的时间
     * @param unit    timeout的单位
     */
    private void advanceClock(long timeout, TimeUnit unit) {
        try {
            Bucket bucket = bucketDelayQueue.poll(timeout, unit);
            if (bucket != null) {
                timeWheel.advanceClock(bucket.getExpire());
                bucket.flush(this::addTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
