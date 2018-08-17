package com.skp.logmetric;

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
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.GeneralConsumer;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigProcess;
import com.skp.logmetric.config.ConfigProcessItem;
import com.skp.logmetric.config.ConfigProcessMatch;
import com.skp.logmetric.config.TypeField;
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
	    GeneralConsumer runnableConsumer = new GeneralConsumer(1, consumer);
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
	
	@Test
	public void testConsumerAccessLogPlain() throws IOException {
	    // Setup consumer
		String topic = "my_topic";
	    GeneralConsumer runnableConsumer = new GeneralConsumer(1, consumer);
	    runnableConsumer.assign(Arrays.asList(new TopicPartition(topic, 0)));
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);

	    // Create record
		ResourceHelper.processResource("com/skp/logmetric/access.log", new LineReadCallback() {
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
	
	@Test
	public void testConsumerAccessLogJson() throws IOException {
	    // Setup consumer
		String topic = "my_topic";
	    GeneralConsumer runnableConsumer = new GeneralConsumer(1, consumer);
	    runnableConsumer.assign(Arrays.asList(new TopicPartition(topic, 0)));
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);

	    // Create record
		ResourceHelper.processResource("com/skp/logmetric/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				long count=0;
				consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0, 
	    				count++, "mykey", produceJson(line)));
			}

		});

	    // Consume
	    runnableConsumer.consume();
	}
	
	private String produceJson(String line) {
		JSONObject j = new JSONObject();
		j.put("host", "web01");
//		j.put("@timestamp", value);
		j.put("log",  line);
		return j.toString();
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
	 * %{WORD:ip} %{WORD:identd} %{WORD:userid} \[%{DATE:date}\] "%{WORD} %{WORD:request} %{WORD}" %{LONG:responseCode} %{LONG:byteSent} "%{DATA:referer}" "%{DATA:client}" "%{DOUBLE:responseTime}"(?:$|\s.*)
	 * (\S+) (\S+) (\S+) \[(.+?)\] "(\S+) (\S+) (\S+)" (\d+) (\d+) "(.*?)" "(.*?)" "([\d\.]+)"(?:$|\s.*)
	 * 
	 * {
	 *   "type": "access",
	 *   "host": "test.com",
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
	public void testProcess() throws IOException, ParseException {
	    // Setup consumer
		String topic = "my_topic";
	    GeneralConsumer runnableConsumer = new GeneralConsumer(1, consumer);
	    runnableConsumer.assign(Arrays.asList(new TopicPartition(topic, 0)));
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);

	    // Create record
		ResourceHelper.processResource("com/skp/logmetric/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				long count=0;
				consumer.addRecord(new ConsumerRecord<String, String>("my_topic", 0, 
	    				count++, "mykey", produceJson(line)));
			}

		});

	    // Consume
	    runnableConsumer.consume();
		
/*		String input = ResourceHelper.getResourceString("process.conf");
		Config config = Config.create(input);
		
		ResourceHelper.processResource("com/skp/logmetric/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				long count=0;
				process(config, line);
			}
		}); */
	}

	private void process(Config config, String line) {
		JSONObject log = new JSONObject();
		
		ConfigProcess configProcess = config.getConfigProcess();
		List<ConfigProcessItem> configProcessList = configProcess.getConfigProcessList();
		for (ConfigProcessItem item : configProcessList) {
			if (item instanceof ConfigProcessMatch)
				processMatch((ConfigProcessMatch) item, line, log);
		}
/*		for (int i=0; i<configProcessList.size(); i++) {
			ConfigProcessItem item = configProcessList.get(i);
			if (item instanceof ConfigProcessMatch)
				processMatch((ConfigProcessMatch) item, line, log);
		} */
		
	}

	private void processMatch(ConfigProcessMatch config, String line, JSONObject log) {
		String regex = config.getPatternRegex();
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(line);
		if (!m.find()) {
			logger.error("Process match fail: line=" + line);
			return;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("Process match ");
		List<TypeField> typeFieldList = config.getTypeFieldList();
		for (TypeField tf : typeFieldList) {
			int pos = tf.getPos();
			String type = tf.getType();
			String value = m.group(pos);
			if (tf.getField() != null) {
				if (TypeField.KEY_LONG.equals(type))
					log.put(tf.getField(), Long.parseLong(value));
				else if (TypeField.KEY_DOUBLE.equals(type))
					log.put(tf.getField(), Double.parseDouble(value));
				else
					log.put(tf.getField(), value);
			}
			sb.append(" " + pos + "=" + m.group(pos));
		}
/*		for (int i=0; i<typeFieldList.size(); i++) {
			TypeField tf = typeFieldList.get(i);
			int pos = tf.getPos();
			String type = tf.getType();
			String value = m.group(pos);
			if (tf.getField() != null) {
				if (TypeField.KEY_LONG.equals(type))
					log.put(tf.getField(), Long.parseLong(value));
				else if (TypeField.KEY_DOUBLE.equals(type))
					log.put(tf.getField(), Double.parseDouble(value));
				else
					log.put(tf.getField(), value);
			}
			sb.append(" " + pos + "=" + m.group(pos));
		} */
//		logger.debug(sb.toString());
		logger.debug(log.toString());
	}
	
}
