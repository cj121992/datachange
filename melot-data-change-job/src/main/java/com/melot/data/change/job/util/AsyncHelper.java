package com.melot.data.change.job.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.melot.data.change.api.ChangeData;
import com.melot.data.change.job.AbstractAsyncStreamRelay;

@Component
public class AsyncHelper {

private final static Logger logger = Logger.getLogger(AsyncHelper.class);
	
	private static final int EXECUTOR_POOL_SIZE = 1; 
	private static final int MAX_NOTIFY_QUEUE_SIZE = 1;
	
	private static final ExecutorService notifyExecutor = new ThreadPoolExecutor(EXECUTOR_POOL_SIZE, 
			EXECUTOR_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(MAX_NOTIFY_QUEUE_SIZE),
            new ThreadFactory(){
				private final AtomicInteger mThreadNum = new AtomicInteger(1);
				@Override
				public Thread newThread(Runnable r) {
					String name = "NotifyExecutorPool-" + mThreadNum.getAndIncrement();
			        Thread th = new Thread(r);
			        th.setName(name);
			        th.setDaemon(true);
			        return th;
				}
			}, 
			new RejectedExecutionHandler(){

				@Override
				public void rejectedExecution(Runnable r,
						ThreadPoolExecutor executor) {
					try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                    	logger.error("NotifyExecutorPool interrupted!!!");
                        throw new RuntimeException(e);
                    }
				}
				
			});
			
	
	public static Future<Boolean> addToThread(final ChangeData data, final AbstractAsyncStreamRelay relay) {
		Future<Boolean> future = notifyExecutor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				relay.stream(data);
				return true;
			}
		});
		return future;
	}
	
}
