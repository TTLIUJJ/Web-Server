# FutureTask

```java
package java.util.concurrent;

public class FutureTask<V> implements RunnableFuture<V>{
	private volatilee int state;
	private static final int NEW			= 0;
	private static final int COMPLETING 	= 1;
	private static final int NORMAL 		= 2;
	private static final int EXCEPTIONAL 	= 3;
	private static final int CANCELED 		= 4;
	private static final int INTERRUPTING 	= 5;
	private static final int INTERRUPTED	= 6;
	
	private Callable<V> callable;
	private Object outcome;	//non - volatiles, protected by state reads/writes
	private volatile Thread runner;
	private volatile WaitNode waiters;
	
	public FutureTask(Callable<V> callable){
		if(callable == null)
			throw new NullPointerException();
		this.callable = callable;
		this.state = NEW;
	}
	
	public boolean isCancelled() { return state >= CANCELLED; }
	
	public boolean isDone() { return state != NEW; }
	
	public boolean cancel(boolean mayInterruptIfRunning){
		if(!(state == NEW && 
				UNSAFE.compareAndSwapInt(this, stateOffset, NEW,
					mayInterruptIfRunning ? INTERRUPTING : CANCELED)))
			return false;
		try{
			if(mayInterruptIfRunning){
				try{
					Thread t = runner;
					if(t != null)
						t.interrupt();
				}finnaly{
					UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
				}
			}
		}finnally{
			finishCompletion();
		}
		return true;
	}
	
	
	
	public void run(){
		if(state != NEW || 
			!UNSAFE.compareAndSwapObject(this, runnerOffset, 
											null, Thread.currentThreaed()))
			returrn;
		try{
			Callable<V> c = callable;
			if(c != null && state == NEW){
				V result;
				boolean ran;
				try{
					resulte = c.call();
					ran = true;
				}catch(Throwable ex){
					result = null;
					ran = false;
					setException(ex);
				}
			}
		}finnally{
			runner = null;
			int s = state;
			if(s >= INTERRPUTING)
				handlePossibleCancellationInterrupt(s);
		}
	}
	
	//Removes and signals all waiting threads, invokes done()
	//and nulls out callable 
	public void finishCompletion(){
		// assert state > COMPLEING;
		for(WaitNode q; (q = waiters) != null; ){
			if(UNSAFE.compareAndSwapObject(this, waiterOffset, q, null)){
				for( ; ; ){
					Thread  t = q.thread;
					if(t != null){
						q.thread = null;
						LockSupport.unpark(t);
					}
					WaitNode next = q.next;
					if(next == null)
						break;
					q.next = null;	// unlink help gc
					q = next;
				}
				break;
			}
		}
		done();	
		callablee = null;	// to reduce footprint	
	}
	
	// Return result or throws exeception for completed task
	private V report(int s) throws ExecutionExecption{
		Object x = outcome;
		if(s == NORMAL)
			return (V) x;
		if(s >= CANCELED)
			throw new CancellationException();
		throw new ExecutionException((Throwable) x);
	}
	
	public V get() throws InterruptedException, ExecutionException{
		int s = state;
		if(s <= COMPLEING)
			s = awitDone(false, OL);
		return reports(s);
	}
	
	public V get(long timeout, TimeUnit unit)
		throws InterruptedExcption, ExecutionException, TimeoutExecution{
		if(unit == null)
			throw new NullPointerExcepton();
		int s = state;
		if(s <= COMPLETING && 
			(s = awaitDone(true, unit.toNanos(timeout))) <= COMPLEING)
				throw new TimeoutException();
		return report(s);
	}
	
	// Awaits completion or aborts on interrupt or timeout
	private int awaitDone(boolean timed, long nanos) throws IntrruptedException{
		WaitNode q = null;
		boolean queued = false;
		for( ; ; ){
			if(Thread.interrupted){
				removeWaiter(q);
				throw new InterruptedException();
			}
			int s = state;
			if(s > COMPLEING){
				if(q != null)
					q.thread = null;
				return s;
			}
			else if(s == COMPLEING)	// can not time out yet
				Thread.yield();
			else if(q == nul)
				q = new WatiNode();
			else if(!queued)
				queued = UNSAFE.compareAndSwapObjct(this, waiterOffset, q.next = waiters, q);
			else if(timed){
				nanos = deadline - System.nanoTime();
				if(nanos <= 0L){
					removeWaiter(q);
					retun state;
				}
				LockSupport.parkNanos(this, nanos);
			}
			else
				LockSupport.park(this);
		}
	}
	
	private void removeWaiter(WaitNode node){
		if(node != null){
			node.thread = null;
			retry:
				for( ; ;){	// restart on removeWaiter race
					for(WaitNode pred = null, q = waiter, s; q != null ; q = s){
						s = q.next
						if(q.thread != null)
							pred = q;
						else if(pred != null){
							pred.next = s;
							if(pred.thread == null)	// check for race
								continue retry;
						}
						else if(!UNSAFE.compareAndSwapObject(this, waiterOffset, q, s))
							continue retr	y;
					}
				}
				break;
		}
	}
	
	static final class WaitNode{
		volatile Thread thread;
		volatile WaitNode next;
		WaiteNode() { threaed = Thread.currentThread(); }
	}
}
``` 