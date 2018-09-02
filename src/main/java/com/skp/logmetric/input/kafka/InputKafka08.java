package com.skp.logmetric.input.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.InputPlugin;
import com.skp.logmetric.input.kafka.GeneralConsumer.ConsumerCallback;
import com.skp.logmetric.process.ProcessQueue;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import lombok.Data;

@Data
public class InputKafka08 implements InputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
/*	ConfigInputKafka config;
	List<GeneralConsumer> consumerList = new ArrayList<>();
	ExecutorService executor; */
	
	ConfigInputKafka08 config;
	List<GeneralConsumer08> consumerList = new ArrayList<>();
	private ConsumerConnector consumerConnector;
//    private String topic;
    ExecutorService executor;

	public InputKafka08(ConfigInputKafka08 config) {
		this.config = config;
	}
	
	public void init() {
        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(config.getZookeeper(), config.getGroup()));
//        this.topic = a_topic;
        
        int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(config.getTopic(), new Integer(numConsumers));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(config.getTopic());
        
 //       List<String> topics = Arrays.asList(config.getTopic());		// Arrays.asList("foo", "bar");
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
        	GeneralConsumer08 consumer = new GeneralConsumer08(stream, threadNumber);
            consumerList.add(consumer);
            threadNumber++;
        }
		
/*        int numConsumers = 1;
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
        } */
		
	}

	public void start() {
		for (GeneralConsumer08 consumer: consumerList)
			executor.submit(consumer);
	}
	
	public void stop() {
        if (consumerConnector != null) consumerConnector.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
/*		executor.shutdown(); */
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

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "3000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
//        props.put("zookeeper.session.timeout.ms", "400");
//        props.put("zookeeper.sync.time.ms", "200");
//        props.put("auto.commit.interval.ms", "1000");
 
        return new ConsumerConfig(props);
    }

	@Override
	public void run() {
	}

}
