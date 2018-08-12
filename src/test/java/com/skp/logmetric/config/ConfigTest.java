package com.skp.logmetric.config;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.testutil.ResourceHelper;

public class ConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testConfig() throws IOException, ParseException {
		String input = ResourceHelper.getResourceString("process.conf");
		Config config = Config.create(input);
		
		List<ConfigProcessItem> configProcessItemList = config.getConfigProcess().getConfigProcessList();
		ConfigProcessMatch configProcessMatch = (ConfigProcessMatch) configProcessItemList.get(0);
		String pattern = configProcessMatch.getPattern();
		logger.debug("pattern=" + configProcessMatch.getPattern());
		logger.debug("patternRegex=" + configProcessMatch.getPatternRegex());
	} 
}