package com.skp.logmetric;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.input.kafka.GeneralConsumer;
import com.skp.logmetric.process.ProcessProcessor;
import com.skp.logmetric.process.ProcessMetricsService;
import com.skp.util.FileHelper;
import com.skp.util.FileHelper.LineReadCallback;

public class GeneralConsumerTest {
	private static final Logger logger = LoggerFactory.getLogger(GeneralConsumerTest.class);
	static String topic = "my_topic";
//	MockConsumer<String, String> kafkaConsumer;
	GeneralConsumer.ConsumerCallback callback = new GeneralConsumer.ConsumerCallback() {
		@Override
		public void consume(int id, ConsumerRecords<String, String> records) {
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("Consumer " + id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
			}
		}
    };
	
	@Before
	public void setUp() {
/*	    kafkaConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    kafkaConsumer.updateBeginningOffsets(beginningOffsets); */
	}
	
	@Test
	public void testConsumer() throws IOException {
		MockConsumer<String, String> mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
				
	    // Setup consumer
	    GeneralConsumer gconsumer = new GeneralConsumer(1, mockConsumer, callback);
	    gconsumer.applyMockConsumer(mockConsumer, topic);
//	    runnableConsumer.assign(topic, Arrays.asList(0));

	    // Create record
	    offset = 0;
	    mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 
	    				offset++, "mykey", "myvalue0"));
	    mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0,
	                    offset++, "mykey", "myvalue1"));
	    mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0,
	                    offset++, "mykey", "myvalue2"));
	    mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0,
	                    offset++, "mykey", "myvalue3"));
	    mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0,
	                    offset++, "mykey", "myvalue4"));

	    // Consume
	    gconsumer.consume();
	}
	
	@Test
	public void testConsumerAccessLogPlain() throws IOException {
		MockConsumer<String, String> mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
		
	    // Setup consumer
	    GeneralConsumer gconsumer = new GeneralConsumer(1, mockConsumer, callback);
	    gconsumer.applyMockConsumer(mockConsumer, topic);
//	    gconsumer.assign(topic, Arrays.asList(0));

	    // Create record
	    generateSamplePlain(mockConsumer, topic);
/*	    offset = 0;
		ResourceHelper.processResource("com/skp/logmetric/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 
	    				offset++, "mykey", line));
			} 
		}); */

	    // Consume
	    gconsumer.consume();
	}
	
	@Test
	public void testConsumerAccessLogJson() throws IOException {
		MockConsumer<String, String> mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
		
	    // Setup consumer
	    GeneralConsumer gconsumer = new GeneralConsumer(1, mockConsumer, callback);
	    gconsumer.applyMockConsumer(mockConsumer, topic);
//	    gconsumer.subscribe(Arrays.asList(topic));
	    
/*	    // Setup Kafka MockConsumer
	    kafkaConsumer.rebalance(Collections.singletonList(new TopicPartition(topic, 0)));
	    kafkaConsumer.seek(new TopicPartition(topic, 0), 0);
//	    runnableConsumer.assign(topic, Arrays.asList(0)); */

	    // Create record
	    generateSampleJson(mockConsumer, topic);

	    // Consume
	    gconsumer.consume();
	}
	
	static long offset;
	public static void generateSamplePlain(MockConsumer<String, String> mockConsumer, String topic) {
		offset = 0;
		FileHelper.processFile("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 
	    				offset++, "mykey", line));
			} 
		});
	}
	
	public static void generateSampleJson(MockConsumer<String, String> mockConsumer, String topic) {
	    offset = 0;
		FileHelper.processFile("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 
	    				offset++, "mykey", produceJson("web01", line)));
				mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0, 
	    				offset++, "mykey", produceJson("web02", line)));
			}
		});
	}

	public static String produceJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("host", host);
		j.put("log",  line);
		return j.toString();
	}

}
