package com.skp.logmetric.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.skp.logmetric.GeneralConsumer;
import com.skp.logmetric.config.ConfigInputKafka;

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
        	GeneralConsumer consumer = GeneralConsumer.createConsumer(i, config.getBroker(), config.getGroup());
            consumerList.add(consumer);
        	
            consumer.subscribe(topics);
            executor.submit(consumer);
        }
	}

}
