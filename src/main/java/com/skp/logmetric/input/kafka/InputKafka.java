package com.skp.logmetric.input.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.skp.logmetric.GeneralConsumer;
import com.skp.logmetric.GeneralConsumer.ConsumerCallback;
import com.skp.logmetric.input.InputPlugin;

public class InputKafka implements InputPlugin {
	ConfigInputKafka config;
	List<GeneralConsumer> consumerList = new ArrayList<>();

	public InputKafka(ConfigInputKafka config) {
		this.config = config;
	}

	@Override
	public boolean process() {
		return true;
	}

	@Override
	public void run() {
        int numConsumers = 1;
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);
        
        List<String> topics = Arrays.asList(config.getTopic());		// Arrays.asList("foo", "bar");
        for (int i = 0; i < numConsumers; i++) {
        	GeneralConsumer consumer = GeneralConsumer.createConsumer(i, config.getBroker(), config.getGroup(), new ConsumerCallback() {
				@Override
				public void consume(int id, ConsumerRecords<String, String> records) {
					for (ConsumerRecord<String, String> record : records) {
						// TODO Consume input
						System.out.println("Consumer " + id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
					}
				}
        	});
            consumerList.add(consumer);
        	
            consumer.subscribe(topics);
            executor.submit(consumer);
        }
	}

}
