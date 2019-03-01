package com.skp.logmetric.process;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.generator.ProcessQueueGenerator;
import com.skp.logmetric.process.ProcessProcessor;
import com.skp.logmetric.process.ProcessMetricsService;

public class ProcessProcessorTest {
//	private static final Logger logger = LoggerFactory.getLogger(ProcessProcessorTest.class);
	
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
	public void testSampleJson() throws IOException, ParseException {
		// Get config
		Config config = Config.createFromResource("process-nxlog.conf", "regex.conf");
		
		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();
		
		// Generate sample log
	    ProcessQueueGenerator.generateSampleCnxlogJson("access.log");
//		generateSampleJson();
		
		// Process
		for (int i=0; i<200; i++)
			pprocess.process();
		
		// Export
		ProcessMetricsService service = new ProcessMetricsService();
	    service.export(0);
	    service.export(0);
	}
	
	/*
	 * Filebeat sample (Filebeat -> Logstash):
	 * {"@timestamp":"2018-09-11T08:53:23.104Z","@version":"1","offset":1488771,"input":{"type":"log"},"tags":["beats_input_codec_plain_applied"],"beat":{"version":"6.4.0","name":"SMONi-web-dev01","hostname":"SMONi-web-dev01"},"prospector":{"type":"log"},"source":"/app/nginx/logs/access.log","message":"172.21.43.140 - - [11/Sep/2018:17:53:15 +0900] \"GET /v1/instances/list HTTP/1.1\" 200 575 \"-\" \"Apache-HttpClient/4.5.2 (Java/1.8.0_51)\" \"0.007\"","host":{"name":"SMONi-web-dev01"}}
	 */
	@Test
	public void testFilebeatJson() throws IOException, ParseException {
		// Get config
		Config config = Config.createFromResource("process-filebeat.conf", "regex.conf");
		
		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();
		
		// Generate sample log
	    ProcessQueueGenerator.generateSampleFilebeatJson("access.log");
//		generateFilebeatJson();
		
		// Process
		for (int i=0; i<200; i++)
			pprocess.process();
		
		// Export
		ProcessMetricsService service = new ProcessMetricsService();
	    service.export(0);
	    service.export(0);
	}

}
