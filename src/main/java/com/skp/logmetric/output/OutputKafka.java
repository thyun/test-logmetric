package com.skp.logmetric.output;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;

import lombok.Data;

@Data
public class OutputKafka implements OutputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ConfigOutputKafka config;
	ExecutorService executor;
	OutputQueue outputQueue = new OutputQueue();

	public OutputKafka(ConfigOutputKafka config) {
		this.config = config;
	}

	@Override
	public void run() {
		while (true)
			process();
	}

	private void process() {
		try {
			List<LogEvent> elist = outputQueue.take();
			for (LogEvent e: elist)
				process(config, e);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} 
	}

	private void process(ConfigOutputKafka config, LogEvent e) {
		logger.debug("output kafka: " + e);

	}

	@Override
	public void init() {
		int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);

	}

	@Override
	public void start() {
		executor.submit(this);

	}

	@Override
	public void stop() {
		executor.shutdown();
	}

}
