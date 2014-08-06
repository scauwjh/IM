package com.java.mina.constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	
	protected final static Integer N_THREAD = 100;
	
	public static ExecutorService executor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors() * N_THREAD);
	
	public static void execute(Thread thread) {
		executor.execute(thread);
	}
}
