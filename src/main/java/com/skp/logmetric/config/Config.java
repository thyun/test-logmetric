package com.skp.logmetric.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.Data;

@Data
public class Config {
	ConfigInput configInput;
	ConfigProcess configProcess;
	ConfigOutput configOutput;
	String foo;
	
	public static Config create(String value) {
		Config config = new Config();
		config.init(value);
		config.prepare();
		return config;
	}
	
	public static Config create(String resourceString, ConfigRegex configRegex) {
		// TODO Auto-generated method stub
		return null;
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
