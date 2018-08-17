package com.skp.logmetric.consumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogConsumer implements Runnable {
	private final int id;
	private final Consumer<String, String> consumer;
	
	public LogConsumer(int id, Consumer<String, String> consumer) {
		this.id = id;
		this.consumer = consumer;
	}
	
	public void subscribe(List<String> topics) {
		consumer.subscribe(topics);
	}
	
	public void assign(List<TopicPartition> partitions) {
	    consumer.assign(partitions);
	}

	@Override
	public void run() {
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				for (ConsumerRecord<String, String> record : records) {
					Map<String, Object> data = new HashMap<>();
					data.put("partition", record.partition());
					data.put("offset", record.offset());
					data.put("value", record.value());
					System.out.println(this.id + ": " + data);
				}
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
	
	public void consume() {
		ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
		for (ConsumerRecord<String, String> record : records) {
			System.out.println("Consumer " + this.id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
		}
	}

}
