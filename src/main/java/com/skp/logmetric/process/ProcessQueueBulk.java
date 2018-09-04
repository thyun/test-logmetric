package com.skp.logmetric.process;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skp.logmetric.event.LogEvent;

import lombok.Data;

@Data
public class ProcessQueueBulk extends LinkedBlockingQueue<List<LogEvent>> {
	final static int QUEUE_SIZE = 1000;
	static ProcessQueueBulk processQueue = null;
	
	public ProcessQueueBulk(int queueSize) {
		super(queueSize);
	}

	public static ProcessQueueBulk getInstance() {
		if (processQueue == null) {
			processQueue = new ProcessQueueBulk(QUEUE_SIZE);
		}
		return processQueue;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProcessQueue: ");
		Iterator<List<LogEvent>> it = this.iterator();
		while (it.hasNext()) {
			List<LogEvent> elist = it.next();
			for (LogEvent e: elist)
				sb.append(" " + e);
			sb.append("\n");
		}
		return sb.toString();
	}
}
