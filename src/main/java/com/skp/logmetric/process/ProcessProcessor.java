package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigItem;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.output.OutputProcessor;

import lombok.Data;

@Data
public class ProcessProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();
	
	private final Config config;
	ArrayList<ConfigItem> cprocessList = new ArrayList<>();
	
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
		for (HashMap<String, Object> cprocess: config.getProcess()) {
			String type = (String) cprocess.get("type");
			if (type.equals("match")) {
				ConfigProcessMatch c = objectMapper.convertValue(cprocess, ConfigProcessMatch.class);
				c.prepare();
				cprocessList.add(c);
			} else if (type.equals("date")) {
				ConfigProcessDate c = objectMapper.convertValue(cprocess, ConfigProcessDate.class);
				c.prepare();
				cprocessList.add(c);
			} else if (type.equals("metrics")) {
				ConfigProcessMetrics c = objectMapper.convertValue(cprocess, ConfigProcessMetrics.class);
				c.prepare();
				cprocessList.add(c);
			}
		}
		
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
		try {
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
        	logger.error("Error", ex);
        }
	}

	public void process() {
		ArrayList<LogEvent> outList = new ArrayList<>();
		try {
			List<LogEvent> elist = ProcessQueue.getInstance().take();
			for (LogEvent e: elist) {
				LogEvent out = process(config, e);
				if (out != null)
					outList.add(out);
			}
		} catch (Exception ex) {
			logger.error("Error", ex);
		}
		
		if (outputProcessor != null && outList.size() > 0)
			outputProcessor.put(outList);
	}
	
	private LogEvent process(Config config, LogEvent e) {
		logger.debug("process() start");
		LogEvent out=e;
		
//		ConfigProcess configProcess = config.getConfigProcess();
//		List<ConfigItem> configProcessList = configProcess.getConfigProcessList();
		for (ConfigItem item : cprocessList) {
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
		logger.debug("ProcessProcessor.process() end");
		return out;
	}

}
