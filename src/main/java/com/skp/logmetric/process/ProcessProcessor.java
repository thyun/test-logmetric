package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.List;
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

public class ProcessProcessor implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final int id;
	private final Consumer<String, String> consumer;
	private final Config config;
	ProcessMatch processMatch = new ProcessMatch();
	ProcessDate processDate = new ProcessDate();
	ProcessMetrics processMetrics = new ProcessMetrics();
	
	public ProcessProcessor(int id, Consumer<String, String> consumer, Config config) {
		this.id = id;
		this.consumer = consumer;
		this.config = config;
	}
	
	public void init() {
	}
	
	public void subscribe(List<String> topics) {
		consumer.subscribe(topics);
	}
	
	public void assign(String topic, List<Integer> partitions) {
		List<TopicPartition> topicPartitions;
		
		topicPartitions = new ArrayList<>();
		for (Integer partition : partitions) {
			topicPartitions.add(new TopicPartition(topic, partition));
		}
	    consumer.assign(topicPartitions);
	}

	@Override
	public void run() {
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				process(records);
			}
		} catch (WakeupException e) {
			// ignore for shutdown 
		} finally {
			consumer.close();
		}
	}

	public void shutdown() {
		consumer.wakeup();
	}
	
	// Used for test
	public void consume() {
		ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
		process(records);
	}

	private void process(ConsumerRecords<String, String> records) {
		for (ConsumerRecord<String, String> record : records) {
			System.out.println("Consumer " + this.id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
			try {
				LogEvent e = LogEvent.parse(record.key(), record.value());
				process(config, e);
			} catch (JSONException e) {
				logger.error("LogConsumer.process() exception: " + e);
			}
		}
	}
	
	private void process(Config config, LogEvent e) {
		
		ConfigProcess configProcess = config.getConfigProcess();
		List<ConfigItem> configProcessList = configProcess.getConfigProcessList();
		for (ConfigItem item : configProcessList) {
			boolean r=true;
			if (item instanceof ConfigProcessMatch)
				r = processMatch.process((ConfigProcessMatch) item, e);
			
			if (item instanceof ConfigProcessDate)
				r = processDate.process((ConfigProcessDate) item, e);
			
			if (item instanceof ConfigProcessMetrics)
				r = processMetrics.process((ConfigProcessMetrics) item, e);
			
//			if (r != true)
//				return;
		}
		
	}

}
