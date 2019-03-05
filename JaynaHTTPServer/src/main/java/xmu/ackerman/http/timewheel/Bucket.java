package xmu.ackerman.http.timewheel;


import java.util.Comparator;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @Program: JaynaHTTPServer
 * @Description:
 * @Author: Ackerman
 * @Create: 2019-03-04 15:45
 */
public class Bucket implements Delayed {
    private AtomicLong sequence;
    private AtomicLong expiration;
    private PriorityBlockingQueue<TimedTask> timedTasksQueue;
//    private LinkedBlockingQueue<TimedTask> timedTaskList;

    Bucket() {
        sequence = new AtomicLong(0);
        expiration = new AtomicLong(-1);
//        timedTaskList = new LinkedBlockingQueue<TimedTask>();
        timedTasksQueue = new PriorityBlockingQueue<>(16, new Comparator<TimedTask>() {
            @Override
            public int compare(TimedTask o1, TimedTask o2) {
                if (o1.getExpireTimestamp() == o2.getExpireTimestamp()) {
                    return Long.compare(o1.getSequence(), o2.getSequence());
                }
                return Long.compare(o1.getExpireTimestamp(), o2.getExpireTimestamp());
            }
        });
    }

    public long getDelay(TimeUnit unit) {
        return Math.max(0, unit.convert(expiration.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    public int compareTo(Delayed o) {
        assert (o instanceof Bucket);
        return Long.compare(getExpire(), ((Bucket)o).getExpire());
    }

    public void flush(Consumer<TimedTask> f) {
        while (!timedTasksQueue.isEmpty()) {
            TimedTask timedTask = timedTasksQueue.poll();
            f.accept(timedTask);
        }

        expiration.set(-1L);
    }

    public boolean setExpiration(long expire) {
        return expiration.getAndSet(expire) != expire;
    }

    public long getExpire() {
        return expiration.get();
    }

    public void addTask(TimedTask timedTask) {
        assert (timedTask.getBucket() != null);
        timedTask.setBucket(this);
        timedTasksQueue.add(timedTask);
    }

    public void removeTask(TimedTask timedTask) {
        assert (timedTask.getBucket() == this);
        timedTask.setBucket(null);
        timedTasksQueue.remove(timedTask);
    }

    public long getSequence() {
        return sequence.get();
    }

    public long getAndIncrement() {
        return sequence.getAndIncrement();
    }
}
