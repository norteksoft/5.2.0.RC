package com.norteksoft.product.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	
	private static ThreadPoolExecutor producerPool;
	
	static{
		// 构造一个线程池
		   producerPool = new ThreadPoolExecutor(Integer.parseInt(readPropertiesFile("thread.corePoolSize")), 
				   Integer.parseInt(readPropertiesFile("thread.maximumPoolSize")),
				   Long.parseLong(readPropertiesFile("thread.keepAliveTime")),
			TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(Integer.parseInt(readPropertiesFile("thread.workQueueNum"))),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	/**
	 * @param task
	 */
	public static void execute(Runnable task) {
		producerPool.execute(task);
	}
	
	private static String readPropertiesFile(String key){
		InputStream  in = ThreadPool.class.getClassLoader().getResourceAsStream("threadConfig.properties");
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			new RuntimeException("threadConfig.properties load fielded.");
		}
		final String value = props.getProperty(key);
		return value;
	}
}
