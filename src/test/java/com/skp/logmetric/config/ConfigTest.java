package com.skp.logmetric.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.KeyValue;
import com.skp.util.ResourceHelper;
import com.skp.util.ResourceHelper.LineReadCallback;

public class ConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);
	
	@Before
	public void setUp() {
	}
	
	// pattern=%{WORD:ip} %{WORD:identd} %{WORD:userid} \[%{DATE:date}\] "%{WORD} %{WORD:request} %{WORD}" %{LONG:responseCode} %{LONG:byteSent} "%{DATA:referer}" "%{DATA:client}" "%{DOUBLE:responseTime}"(?:$|\s.*)
	// patternRegex=(\S+) (\S+) (\S+) \[(.+?)\] "(\S+) (\S+) (\S+)" (\d+) (\d+) "(.*?)" "(.*?)" "([\d\.]+)"(?:$|\s.*)
	@Test
	public void testConfig() throws IOException, ParseException {
		ConfigRegex configRegex = ConfigRegex.create(ResourceHelper.getResourceLineList("regex.conf"));
		Config config = Config.create(ResourceHelper.getResourceString("process.conf"));
		
		List<ConfigProcessItem> configProcessItemList = config.getConfigProcess().getConfigProcessList();
		ConfigProcessMatch configProcessMatch = (ConfigProcessMatch) configProcessItemList.get(0);
		logger.debug("pattern=" + configProcessMatch.getPattern());
		logger.debug("regexConfig=" + configRegex);
		logger.debug("patternRegex=" + configProcessMatch.getPatternRegex());
	} 
}