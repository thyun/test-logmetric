package com.skp.logmetric.config;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.util.FileHelper;

public class ConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);
	
	@Before
	public void setUp() {
	}
	
	// pattern=%{WORD:ip} %{WORD:identd} %{WORD:userid} \[%{DATE:date}\] "%{WORD} %{WORD:request} %{WORD}" %{LONG:responseCode} %{LONG:byteSent} "%{DATA:referer}" "%{DATA:client}" "%{DOUBLE:responseTime}"(?:$|\s.*)
	// patternRegex=(\S+) (\S+) (\S+) \[(.+?)\] "(\S+) (\S+) (\S+)" (\d+) (\d+) "(.*?)" "(.*?)" "([\d\.]+)"(?:$|\s.*)
	@Test
	public void testConfig() throws IOException {
		ConfigRegex configRegex = ConfigRegex.create(FileHelper.getFileLineListFromResource("regex.conf"));
		logger.debug("confRegex=" + configRegex);
		
		Config config = Config.createFromResource("process-nxlog.conf", "regex.conf");
//		List<ConfigItem> configInputList = config.getConfigInput().getConfigInputList();
//		logger.debug("configInputList=" + configInputList);
		
//		List<ConfigItem> configProcessList = config.getConfigProcess().getConfigProcessList();
//		ConfigProcessMatch configProcessMatch = (ConfigProcessMatch) configProcessList.get(0);
//		logger.debug("pattern=" + configProcessMatch.getPattern());
//		logger.debug("patternRegex=" + configProcessMatch.getPatternRegex());
		logger.debug("config=" + config);
	}
	
	@Test
	public void testConfigValue() {
		String s = "{\"date\":\"31/Jul/2018:17:48:29 +0900\",\"request\":\"/assets/PMON_icon1-a6c18ea37d8809bb7521e9594e7e758e.png?20180528\",\"referer\":\"http://pmon-dev.skplanet.com/hosts?f_field=hostname&f_service=&search=SMONi\",\"responseTime\":0.01,\"ip\":\"10.202.212.58\",\"identd\":\"-\",\"userid\":\"-\",\"byteSent\":2687,\"tags\":[\"beats_input_codec_plain_applied\"],\"responseCode\":200,\"@timestamp\":\"2018-09-13T09:06:45.151Z\",\"beat\":{\"hostname\":\"web01\",\"name\":\"web01\",\"version\":\"6.4.0\"},\"host\":{\"name\":\"web01\"},\"client\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36\"}";
		LogEvent e = new LogEvent(s);
		ConfigValue cv = ConfigValue.create(e, "request");
		logger.debug("cv=" + cv);
		cv = ConfigValue.create(e, "%{request}");
		logger.debug("cv=" + cv);
		cv = ConfigValue.create(e, "%{[host][name]}");
		logger.debug("cv=" + cv);
	}

}
