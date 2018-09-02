package com.skp.logmetric.input.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.InputPlugin;
import com.skp.logmetric.input.kafka.GeneralConsumer.ConsumerCallback;
import com.skp.logmetric.process.ProcessQueue;

import lombok.Data;

@Data
public class InputKafka implements InputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ConfigInputKafka config;
	List<GeneralConsumer> consumerList = new ArrayList<>();
	ExecutorService executor;

	public InputKafka(ConfigInputKafka config) {
		this.config = config;
	}
	
	public void init() {
        int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        
        List<String> topics = Arrays.asList(config.getTopic());		// Arrays.asList("foo", "bar");
        for (int i = 0; i < numConsumers; i++) {
        	GeneralConsumer consumer = GeneralConsumer.createConsumer(i, config.getBroker(), config.getGroup(), new ConsumerCallback() {
				@Override
				public void consume(int id, ConsumerRecords<String, String> records) {
					process(id, records);
				}
        	});
            consumerList.add(consumer);
        	
            consumer.subscribe(topics);
        }
		
	}

	public void start() {
		for (GeneralConsumer consumer: consumerList)
			executor.submit(consumer);
	}
	
	public void stop() {
		executor.shutdown();
	}
	
	private void process(int id, ConsumerRecords<String, String> records) {
		for (ConsumerRecord<String, String> record : records) {
			logger.debug("Consumer " + id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
			try {
				ProcessQueue.getInstance().put(createLogEvent(record.key(), record.value()));
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
		}
	}

	private LogEvent createLogEvent(String key, String value) {
		return LogEvent.parse(key, value);
	}

	@Override
	public void run() {
	}

}
