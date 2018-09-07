package com.skp.logmetric.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

import com.skp.logmetric.GeneralConsumerTest;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.InputProcessor;
import com.skp.logmetric.input.kafka.GeneralConsumer;
import com.skp.logmetric.input.kafka.GeneralConsumer.ConsumerCallback;
import com.skp.logmetric.process.ProcessProcessor;
import com.skp.logmetric.process.ProcessMetricsService;
import com.skp.util.ResourceHelper;
import com.skp.util.ResourceHelper.LineReadCallback;

public class ProcessProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(ProcessProcessorTest.class);
	
	@Before
	public void setUp() {
	}
	
	/*
	 * Logstash grok pattern: (https://github.com/elastic/logstash/blob/v1.4.2/patterns/grok-patterns)
	 * COMMONAPACHELOG %{IPORHOST:clientip} %{USER:ident} %{USER:auth} \[%{HTTPDATE:timestamp}\] "(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})" %{NUMBER:response} (?:%{NUMBER:bytes}|-)
	 * COMBINEDAPACHELOG %{COMMONAPACHELOG} %{QS:referrer} %{QS:agent}
	 * 
	 * ServiceMon KeyType pattern:
	 * %{ip:WORD}\s+%{identd:WORD}\s+%{userid:WORD}\s+\[%{date:DATE}\]\s+"%{request:TEXT}"\s+%{responseCode:WORD}\s+%{byteSent:NUMBER}\s+"%{referer:TEXT}"\s+"%{client:TEXT}"\s+"?%{responseTime:NUMBER}"?(?:$|\s.*)
	 *
	 * Logmetric sample & pattern & pattern regex:
	 * 10.202.212.58 - - [31/Jul/2018:17:48:29 +0900] "GET /assets/PMON_icon1-a6c18ea37d8809bb7521e9594e7e758e.png?20180528 HTTP/1.1" 200 2687 "http://pmon-dev.skplanet.com/hosts?f_field=hostname&f_service=&search=SMONi" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36" "0.010"
	 * %{WORD:ip} %{WORD:identd} %{WORD:userid} \[%{DATE:date}\] "%{WORD} %{WORD:request} %{WORD}" %{LONG:responseCode} %{LONG:byteSent} "%{DATA:referer}" "%{DATA:client}" "%{DOUBLE:responseTime}"(?:$|\s.*)
	 * (\S+) (\S+) (\S+) \[(.+?)\] "(\S+) (\S+) (\S+)" (\d+) (\d+) "(.*?)" "(.*?)" "([\d\.]+)"(?:$|\s.*)
	 * 
	 * {
	 * 	 "sampling": 10,
	 *   "host": "test.com",
	 *   "type": "access",
	 *   "@timestamp": "2018-08-22T08:37:09.850Z",
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
	public void testProcessProcessor() throws IOException, ParseException {
		// Get config
		String input = ResourceHelper.getResourceString("process.conf");
		Config config = Config.create(input);
		
		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();
		
		// Generate sample log
		generateSampleJson();
		
		// Process
		for (int i=0; i<200; i++)
			pprocess.process();
		
		// Export
		ProcessMetricsService service = new ProcessMetricsService();
	    service.export(0);
	    service.export(0);
	}
	
	static long offset;
	public static void generateSampleJson() {
	    offset = 0;
		ResourceHelper.processResource("com/skp/logmetric/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				try {
					List<LogEvent> elist1 = createLogEventList(produceJson("web01", line));
					List<LogEvent> elist2 = createLogEventList(produceJson("web02", line));
					ProcessQueueBulk.getInstance().put(elist1);
					ProcessQueueBulk.getInstance().put(elist2);
//					ProcessQueue.getInstance().put(createLogEvent(produceJson("web01", line)));
//					ProcessQueue.getInstance().put(createLogEvent(produceJson("web02", line)));
				} catch (InterruptedException e) {
					logger.error(e.toString());
				}
			}
		});
	}
	
	protected static List<LogEvent> createLogEventList(String value) {
		ArrayList<LogEvent> elist = new ArrayList<>();
		elist.add(LogEvent.parse(value));
		return elist;
	}

	private static String produceJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("hostname", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	}

}
