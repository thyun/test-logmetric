package com.skp.logmetric.output.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.output.OutputPlugin;
import com.skp.logmetric.output.OutputQueue;

import kafka.producer.KeyedMessage;
import lombok.Data;

@Data
public class OutputKafka08 implements OutputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ConfigOutputKafka08 config;
	ExecutorService executor;
	OutputQueue outputQueue = new OutputQueue();
	GeneralProducer08 producer;

	public OutputKafka08(ConfigOutputKafka08 config) {
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
			process(config, elist);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} 
	}

	private void process(ConfigOutputKafka08 config, List<LogEvent> elist) {
		ArrayList<KeyedMessage<String, String>> mlist = new ArrayList<KeyedMessage<String, String>>();
		for (LogEvent e: elist) {
			logger.debug("output kafka08: " + e);
			KeyedMessage<String, String> m = new KeyedMessage<>(config.getTopic(), "", e.toString());
			mlist.add(m);
		}
		producer.produce(mlist);
	}
	
	@Override
	public void init() {
		int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        
        producer = new GeneralProducer08(config.getBroker());
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
