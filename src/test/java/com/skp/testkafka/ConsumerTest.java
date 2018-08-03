package com.skp.testkafka;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;

import com.skp.testkafka.RunnableConsumer;
import com.skp.testutil.ResourceHelper;
import com.skp.testutil.ResourceHelper.LineReadCallback;

public class ConsumerTest {
	MockConsumer<String, String> consumer;
	
	@Before
	public void setUp() {
	    consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
	}
	
	@Test
	public void testConsumer() throws IOException {
	    // Setup consumer
		String topic = "my_topic";
	    RunnableConsumer runnableConsumer = new RunnableConsumer(1, consumer);
	    runnableConsumer.assign(Arrays.asList(new TopicPartition(topic, 0)));
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);

	    // Create record
	    consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0, 
	    				0L, "mykey", "myvalue0"));
	    consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0,
	                    1L, "mykey", "myvalue1"));
	    consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0,
	                    2L, "mykey", "myvalue2"));
	    consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0,
	                    3L, "mykey", "myvalue3"));
	    consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0,
	                    4L, "mykey", "myvalue4"));

	    // Consume
	    runnableConsumer.consume();
	}
	
	/*
	Logstash grok pattern:
	COMMONAPACHELOG %{IPORHOST:clientip} %{USER:ident} %{USER:auth} \[%{HTTPDATE:timestamp}\] "(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})" %{NUMBER:response} (?:%{NUMBER:bytes}|-)
	COMBINEDAPACHELOG %{COMMONAPACHELOG} %{QS:referrer} %{QS:agent}
	ServiceMon expression:
	%{ip:WORD}\s+%{identd:WORD}\s+%{userid:WORD}\s+\[%{date:DATE}\]\s+"%{request:TEXT}"\s+%{responseCode:WORD}\s+%{byteSent:NUMBER}\s+"%{referer:TEXT}"\s+"%{client:TEXT}"\s+"?%{responseTime:NUMBER}"?(?:$|\s.*)
	*/
	@Test
	public void testConsumerAccessLog() throws IOException {
	    // Setup consumer
		String topic = "my_topic";
	    RunnableConsumer runnableConsumer = new RunnableConsumer(1, consumer);
	    runnableConsumer.assign(Arrays.asList(new TopicPartition(topic, 0)));
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);

	    // Create record
		ResourceHelper.processResource("com/skp/testkafka/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				long count=0;
				consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0, 
	    				count++, "mykey", line));
			} 
		});

	    // Consume
	    runnableConsumer.consume();
	}
	
}
