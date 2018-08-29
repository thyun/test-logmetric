package com.skp.logmetric.config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.process.ConfigProcessMatch;
import com.skp.util.ResourceHelper;

public class ConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);
	
	@Before
	public void setUp() {
	}
	
	// pattern=%{WORD:ip} %{WORD:identd} %{WORD:userid} \[%{DATE:date}\] "%{WORD} %{WORD:request} %{WORD}" %{LONG:responseCode} %{LONG:byteSent} "%{DATA:referer}" "%{DATA:client}" "%{DOUBLE:responseTime}"(?:$|\s.*)
	// patternRegex=(\S+) (\S+) (\S+) \[(.+?)\] "(\S+) (\S+) (\S+)" (\d+) (\d+) "(.*?)" "(.*?)" "([\d\.]+)"(?:$|\s.*)
	@Test
	public void testConfig() throws IOException {
		ConfigRegex configRegex = ConfigRegex.create(ResourceHelper.getResourceLineList("regex.conf"));
		logger.debug("confRegex=" + configRegex);
		
		Config config = Config.create(ResourceHelper.getResourceString("process.conf"));
		List<ConfigItem> configInputList = config.getConfigInput().getConfigInputList();
		logger.debug("configInputList=" + configInputList);
		
		List<ConfigItem> configProcessList = config.getConfigProcess().getConfigProcessList();
		ConfigProcessMatch configProcessMatch = (ConfigProcessMatch) configProcessList.get(0);
		logger.debug("pattern=" + configProcessMatch.getPattern());
		logger.debug("patternRegex=" + configProcessMatch.getPatternRegex());
	}
	
	@Test
	public void testDate() throws ParseException {
		String v = "31/Jul/2018:09:00:00";
		String pattern = "dd/MMM/yyyy:HH:mm:ss";
//		String v = "31/Jul/2018:09:00:00 +0900";
//		String pattern = "dd/MMM/yyyy:HH:mm:ss Z";
		SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.ENGLISH);
		
		Date timestamp = fmt.parse(v);
		logger.debug("timestamp=" + timestamp);
	}
}