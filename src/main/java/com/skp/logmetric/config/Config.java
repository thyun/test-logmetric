package com.skp.logmetric.config;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skp.util.FileHelper;

import lombok.Getter;

@Getter
public class Config {
	static ConfigRegex configRegex = null;
	ConfigInput configInput;
	ConfigProcess configProcess;
	ConfigOutput configOutput;
	
	public static ConfigRegex getConfigRegex() {
		return configRegex;
	}
	
	public static Config create() {
		if (!check(ConfigPath.getProcessConf(), ConfigPath.getRegexConf()))
			return null;
		String processConfStr = FileHelper.getFileFromPath(ConfigPath.getProcessConf());
		List<String> regexConfStrList = FileHelper.getFileLineListFromPath(ConfigPath.getRegexConf());
		configRegex = ConfigRegex.create(regexConfStrList);
		
		Config config = new Config();
		config.init(processConfStr);
		config.prepare();
		
		return config;
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
		
		Config config = new Config();
		config.init(processConfStr);
		config.prepare();
		
		return config;
	}
	
	private static boolean checkFromResource(String processConfPath, String regexConfPath) {
		if (FileHelper.getFileFromResource(processConfPath) == null || FileHelper.getFileFromResource(regexConfPath) == null)
			return false;
		return true;
	}
	
	private void init(String value) {
		JSONObject j = new JSONObject(value);
//		j.put("pattern", "%{WORD:ip} %{WORD:identd} %{WORD:userid} \\[%{DATE:date}\\] \\\"%{WORD} %{WORD:request} %{WORD}\\\" %{LONG:responseCode} %{LONG:byteSent} \\\"%{DATA:referer}\\\" \\\"%{DATA:client}\\\" \\\"%{DOUBLE:responseTime}\\\"(?:$|\\s.*)");
		init(j);
	}

	private void init(JSONObject j) {	
		configInput = new ConfigInput((JSONObject) j.get("input"));
		configProcess = new ConfigProcess((JSONArray) j.get("process"));
		configOutput = new ConfigOutput((JSONObject) j.get("output"));
	}

	public void prepare() {
		configInput.prepare();
		configProcess.prepare();
		configOutput.prepare();
	}

}
