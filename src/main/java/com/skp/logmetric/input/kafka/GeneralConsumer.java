package com.skp.logmetric.input.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.util.FileHelper;
import com.skp.util.FileHelper.LineReadCallback;

import lombok.Data;

@Data
public class GeneralConsumer implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final int id;
	Consumer<String, String> kafkaConsumer;
	ConsumerCallback callback = null;
	
    public interface ConsumerCallback {
    	void consume(int id, ConsumerRecords<String, String> records);
    }
	
	public GeneralConsumer(int id, Consumer<String, String> kafkaConsumer, ConsumerCallback callback) {
		this.id = id;
		this.kafkaConsumer = kafkaConsumer;
		this.callback = callback;
	}
	
	public static GeneralConsumer createConsumer(int id, String broker, String groupId, ConsumerCallback callback) {
    	KafkaConsumer<String, String> kafkaConsumer = GeneralConsumer.createKafkaConsumer(broker, groupId);
        return new GeneralConsumer(id, kafkaConsumer, callback);
    }
	
    public static GeneralConsumer createConsumer(int id, String broker, String groupId) {
    	KafkaConsumer<String, String> kafkaConsumer = GeneralConsumer.createKafkaConsumer(broker, groupId);
        return new GeneralConsumer(id, kafkaConsumer, null);
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
		kafkaConsumer.subscribe(topics);
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
	    kafkaConsumer.assign(topicPartitions);
	}

	@Override
	public void run() {
		try {
			while (true) {
				consume();
			}
		} catch (WakeupException e) {
			logger.error(e.toString()); 
		} finally {
			kafkaConsumer.close();
		}
	}

	public void shutdown() {
		kafkaConsumer.wakeup();
	}
	
	public void consume() {
		ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
		if (callback != null)
			callback.consume(this.id, records);
	}
	
/*	private void process(ConsumerRecords<String, String> records) {
		for (ConsumerRecord<String, String> record : records) {
			System.out.println("Consumer " + this.id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
		}
	} */

	// For test
	public void applyMockConsumer(MockConsumer<String, String> mockConsumer, String topic) {
	    this.kafkaConsumer = mockConsumer;

	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    mockConsumer.updateBeginningOffsets(beginningOffsets);
	    
	    // Assign
	    assign(topic, Arrays.asList(0));
	    
	    // Subscribe & rebalance
/*	    subscribe(Arrays.asList(topic));
	    mockConsumer.rebalance(Collections.singletonList(new TopicPartition(topic, 0)));
	    mockConsumer.seek(new TopicPartition(topic, 0), 0); */
	}

}
