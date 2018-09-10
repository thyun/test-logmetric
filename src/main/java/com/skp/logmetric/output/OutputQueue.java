package com.skp.logmetric.output;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skp.logmetric.event.LogEvent;

import lombok.Data;
import lombok.Getter;

@Getter
public class OutputQueue extends LinkedBlockingQueue<List<LogEvent>> {
	final static int QUEUE_SIZE = 1000;
	
	public OutputQueue() {
		super(QUEUE_SIZE);
	}
	
	public OutputQueue(int queueSize) {
		super(queueSize);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("OutputQueue: ");
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
