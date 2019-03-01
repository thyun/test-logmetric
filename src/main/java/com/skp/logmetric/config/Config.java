package com.skp.logmetric.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.util.CommonHelper;
import com.skp.util.FileHelper;

import lombok.Data;

@Data
public class Config {
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	static ObjectMapper objectMapper = new ObjectMapper();
	
	static ConfigRegex configRegex = null;
	
	HashMap<String, Object> input;
	List<HashMap<String, Object>> process;
	HashMap<String, Object> output;
	
	public static ConfigRegex getConfigRegex() {
		return configRegex;
	}
	
	public static Config create() {
		if (!check(ConfigPath.getProcessConfPath(), ConfigPath.getRegexConfPath()))
			return null;
		String processConfStr = FileHelper.getFileFromPath(ConfigPath.getProcessConfPath());
		List<String> regexConfStrList = FileHelper.getFileLineListFromPath(ConfigPath.getRegexConfPath());
		configRegex = ConfigRegex.create(regexConfStrList);
		
		try {
			Config config = objectMapper.readValue(processConfStr, Config.class);
			return config;
		} catch (IOException e) {	// JsonParseException, JsonMappingException or IOException
			logger.error(CommonHelper.exception2Str(e));
		}
		return null;
	}
	
	private static boolean check(String processConfPath, String regexConfPath) {
		if (!FileHelper.exist(processConfPath) || !FileHelper.exist(regexConfPath))
			return false;
		return true;
	}
	
	// For test
	public static Config createFromResource(String processConfPath, String regexConfPath) {
		if (!checkFromResource(processConfPath, regexConfPath))
			return null;
		String processConfStr = FileHelper.getFileFromResource(processConfPath);
		List<String> regexConfStrList = FileHelper.getFileLineListFromResource(regexConfPath);
		configRegex = ConfigRegex.create(regexConfStrList);
		
		try {
			Config config = objectMapper.readValue(processConfStr, Config.class);
			return config;
		} catch (IOException e) {	// JsonParseException, JsonMappingException or IOException
			logger.error(CommonHelper.exception2Str(e));
		}
		return null;
	}
	
	private static boolean checkFromResource(String processConfPath, String regexConfPath) {
		if (FileHelper.getFileFromResource(processConfPath) == null || FileHelper.getFileFromResource(regexConfPath) == null)
			return false;
		return true;
	}

}
