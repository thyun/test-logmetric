package com.skp.logmetric.output.kafka;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.output.OutputPlugin;
import com.skp.logmetric.output.OutputQueue;

import lombok.Data;

@Data
public class OutputKafka implements OutputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ConfigOutputKafka config;
	ExecutorService executor;
	OutputQueue outputQueue = new OutputQueue();
	GeneralProducer producer;

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
			process(config, elist);
		} catch (InterruptedException ex) {
			logger.error("Error", ex);
		} 
	}

	private void process(ConfigOutputKafka config, List<LogEvent> elist) {
		for (LogEvent e: elist) {
			logger.debug("Output kafka: " + e);
			ProducerRecord<String, String> record = new ProducerRecord<>(config.getTopic(), "", e.toString());
			producer.produce(record);
		}

	}

	@Override
	public void init() {
		int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        
        producer = new GeneralProducer(config.getBroker());

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
