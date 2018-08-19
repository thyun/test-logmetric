package com.skp.logmetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

public class PartitionConsumerOld implements Runnable {
	private final KafkaConsumer<String, String> consumer;
	private final List<TopicPartition> topicPartitions;
	private final List<String> topics;
	private final int id;

	public PartitionConsumerOld(int id,
			String broker,
			String groupId, 
			String topic,
			int partition) {
		this.id = id;
		this.topics = Arrays.asList(topic);
		Properties props = new Properties();
		props.put("bootstrap.servers", broker);
		props.put("group.id", groupId);
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		props.put("session.timeout.ms", "30000");
		this.consumer = new KafkaConsumer<>(props);
		
		this.topicPartitions = new ArrayList<>();
		for (PartitionInfo partitionInfo : consumer.partitionsFor(topic)) {
			System.out.println("partitionInfo partition=" + partitionInfo.partition());
			if (partitionInfo.partition() == partition)
				topicPartitions.add(new TopicPartition(topic, partitionInfo.partition()));
		}
	}

	@Override
	public void run() {
		try {
			consumer.assign(topicPartitions); 
//			consumer.subscribe(topics);

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

}
