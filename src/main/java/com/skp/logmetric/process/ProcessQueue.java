package com.skp.logmetric.process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skp.logmetric.event.LogEvent;

public class ProcessQueue extends LinkedBlockingQueue<LogEvent> {
	final static int QUEUE_SIZE = 1000;
	static ProcessQueue processQueue = null;
//	BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
	
	public ProcessQueue(int queueSize) {
		super(queueSize);
	}

	public static ProcessQueue getInstance() {
		if (processQueue == null) {
			processQueue = new ProcessQueue(QUEUE_SIZE);
		}
		return processQueue;
	}
	

}
