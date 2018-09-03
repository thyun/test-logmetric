package com.skp.logmetric.process;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skp.logmetric.event.LogEvent;

import lombok.Data;

@Data
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProcessQueue: ");
		Iterator<LogEvent> it = this.iterator();
		while (it.hasNext()) {
			LogEvent e = it.next();
			sb.append(" " + e);
		}
		return sb.toString();
	}
}
