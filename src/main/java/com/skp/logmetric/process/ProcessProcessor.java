package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigProcess;
import com.skp.logmetric.config.ConfigItem;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.kafka.GeneralConsumer;
import com.skp.logmetric.output.OutputProcessor;

import lombok.Data;

@Data
public class ProcessProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Config config;
	ProcessMatch processMatch = new ProcessMatch();
	ProcessDate processDate = new ProcessDate();
	ProcessMetrics processMetrics = new ProcessMetrics();
	List<ProcessConsumer> consumerList = new ArrayList<>();
	ExecutorService executor;
	OutputProcessor outputProcessor;
	
	class ProcessConsumer implements Runnable {
		@Override
		public void run() {
			logger.debug("ProcessConsumer.run() start");
			while (true) {
				process();
			}
			
		}
	}
	
	public ProcessProcessor(Config config) {
		this.config = config;
	}
	
	public void init() {
		int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        for (int i=0; i<numConsumers; i++) {
        	consumerList.add(new ProcessConsumer());
        }
	}
	
	public void start() {
		for (ProcessConsumer consumer: consumerList)
			executor.submit(consumer);
	}
	
	public void stop() {
		executor.shutdown();
	}

	public void process() {
		ArrayList<LogEvent> outList = new ArrayList<>();
		try {
			List<LogEvent> elist = ProcessQueueBulk.getInstance().take();
			for (LogEvent e: elist) {
				LogEvent out = process(config, e);
				if (out != null)
					outList.add(out);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (outputProcessor != null && outList.size() > 0)
			outputProcessor.put(outList);
	}
	
	private LogEvent process(Config config, LogEvent e) {
		logger.debug("process() start");
		LogEvent out=e;
		
		ConfigProcess configProcess = config.getConfigProcess();
		List<ConfigItem> configProcessList = configProcess.getConfigProcessList();
		for (ConfigItem item : configProcessList) {
			boolean r=true;
			if (item instanceof ConfigProcessMatch)
				r = processMatch.process((ConfigProcessMatch) item, e);
			
			if (item instanceof ConfigProcessDate)
				r = processDate.process((ConfigProcessDate) item, e);
			
			if (item instanceof ConfigProcessMetrics) {
				r = processMetrics.process((ConfigProcessMetrics) item, e);
				out = null;
			}
			
//			if (r != true)
//				return;
		}
		return out;
		
	}

}
