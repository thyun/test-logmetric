package com.skp.logmetric.output.kafka;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class GeneralProducer {
	String broker;
	Producer<String, String> producer;
	ExecutorService executor;

	public GeneralProducer(String broker) {
		super();
		this.broker = broker;
		
        Properties props = new Properties();
        props.put("bootstrap.servers", broker);
/*        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432); */
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
        producer = new KafkaProducer<>(props);
      
	}
	
	public void produce(ProducerRecord<String, String> record) {
		producer.send(record);
	} 
	
}
