package com.skp.logmetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

public class GeneralConsumer implements Runnable {
	private final int id;
	private final Consumer<String, String> consumer;
	
	public GeneralConsumer(int id, Consumer<String, String> consumer) {
		this.id = id;
		this.consumer = consumer;
	}
	
    public static GeneralConsumer createConsumer(int id, String broker, String groupId) {
    	KafkaConsumer<String, String> kafkaConsumer = GeneralConsumer.createKafkaConsumer(broker, groupId);
        return new GeneralConsumer(id, kafkaConsumer);
    }
	
    private static KafkaConsumer<String, String> createKafkaConsumer(String broker, String groupId) {
		Properties props = new Properties();
		props.put("bootstrap.servers", broker);
		props.put("group.id", groupId);
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		props.put("session.timeout.ms", "30000");
		return new KafkaConsumer<>(props);
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
/*		for (PartitionInfo partitionInfo : consumer.partitionsFor(topic)) {
			System.out.println("partitionInfo partition=" + partitionInfo.partition());
			for (Integer partition : partitions) {
				if (partitionInfo.partition() == partition)
					topicPartitions.add(new TopicPartition(topic, partitionInfo.partition()));
			}
		} */
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
		}
	}

}
