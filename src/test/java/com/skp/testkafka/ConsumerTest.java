package com.skp.testkafka;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.testkafka.RunnableConsumer;
import com.skp.testutil.ResourceHelper;
import com.skp.testutil.ResourceHelper.LineReadCallback;

public class ConsumerTest {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerTest.class);
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
	
	/*
	 * Logstash grok pattern: (https://github.com/elastic/logstash/blob/v1.4.2/patterns/grok-patterns)
	 * COMMONAPACHELOG %{IPORHOST:clientip} %{USER:ident} %{USER:auth} \[%{HTTPDATE:timestamp}\] "(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})" %{NUMBER:response} (?:%{NUMBER:bytes}|-)
	 * COMBINEDAPACHELOG %{COMMONAPACHELOG} %{QS:referrer} %{QS:agent}
	 * 
	 * ServiceMon KeyType expression:
	 * %{ip:WORD}\s+%{identd:WORD}\s+%{userid:WORD}\s+\[%{date:DATE}\]\s+"%{request:TEXT}"\s+%{responseCode:WORD}\s+%{byteSent:NUMBER}\s+"%{referer:TEXT}"\s+"%{client:TEXT}"\s+"?%{responseTime:NUMBER}"?(?:$|\s.*)
	 *
	 * Logmetric sample & pattern & output metric:
	 * 10.202.212.58 - - [31/Jul/2018:17:48:29 +0900] "GET /assets/PMON_icon1-a6c18ea37d8809bb7521e9594e7e758e.png?20180528 HTTP/1.1" 200 2687 "http://pmon-dev.skplanet.com/hosts?f_field=hostname&f_service=&search=SMONi" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36" "0.010"
     *
	 * %{WORD:ip} %{WORD:identd} %{WORD:userid} \[%{DATE:date}\] \"%{WORD} %{WORD:request} %{WORD}\" %{LONG:responseCode} %{LONG:byteSent} \"%{DATA:referer}\" \"%{DATA:client}\" \"%{DOUBLE:responseTime}\"(?:$|\s.*)
	 * (\S+) (\S+) (\S+) \[(.+?)\] \"(\S+) (\S+) (\S+)\" (\d+) (\d+) "(.*?)" "(.*?)" "([\d\.]+)"(?:$|\s.*)
	 * 
	 * {
	 *   "type": "access",
	 *   "count": 10,
	 *   "responseCode.sum": 10000,
	 *   "responseCode.min": 1000,
	 *   "responseCode.max": 5000,
	 *   "responseCode.200": 8,
	 *   "responseCode.400": 1,
	 *   "responseCode.500": 1,
	 *   "byteSent.sum": 10000,
	 *   "byteSent.min": 1000,
	 *   "byteSent.max": 5000,
	 *   "byteSent.1000": 8,
	 *   "byteSent.10000": 1,
	 *   "byteSent.100000": 1
	 * }
	 */
	@Test
	public void testPattern() throws IOException {
		ResourceHelper.processResource("com/skp/testkafka/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				long count=0;
				match(line);
			} 
		});
	}
	
	private void match(String line) {
		String regex = "(\\S+) (\\S+) (\\S+) \\[(.+?)\\] \\\"(\\S+) (\\S+) (\\S+)\\\" (\\d+) (\\d+) \"(.*?)\" \"(.*?)\" \"([\\d\\.]+)\"(?:$|\\s.*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(line);
		if (m.find()) {
			logger.debug("1=" + m.group(1) + ", 2=" + m.group(2) + ", 3=" + m.group(3) +
					", 4=" + m.group(4) + ", 5=" + m.group(5) + ", 6=" + m.group(6) + ", 7=" + m.group(7));
			logger.debug("8=" + m.group(8) + ", 9=" + m.group(9) + ", 10=" + m.group(10) + ", 11=" + m.group(11) + ", 12=" + m.group(12));
		}
		
	}
	
}
