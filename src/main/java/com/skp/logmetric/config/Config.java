package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.skp.testutil.ResourceHelper;

import lombok.Data;

@Data
public class Config {
	JSONObject j;
	ConfigInput configInput;
	ConfigProcess configProcess;
	ConfigOutput configOutput;
	
	public static Config parse(String value) throws ParseException {
		Config config = new Config();
		config.init(value);
		return config;
	}
	
	private void init(String value) throws ParseException {
		
		JSONParser parser = new JSONParser();
		JSONObject j = (JSONObject) parser.parse(value);
		j.put("pattern", "%{WORD:ip} %{WORD:identd} %{WORD:userid} \\[%{DATE:date}\\] \\\"%{WORD} %{WORD:request} %{WORD}\\\" %{LONG:responseCode} %{LONG:byteSent} \\\"%{DATA:referer}\\\" \\\"%{DATA:client}\\\" \\\"%{DOUBLE:responseTime}\\\"(?:$|\\s.*)");
		init(j);
	}

	private void init(JSONObject j) {
		this.j = j;
		
		initInput((JSONObject) j.get("input"));
		initProcess((JSONArray) j.get("process"));
		initOutput((JSONObject) j.get("output"));
	}

	private void initInput(JSONObject j) {
		configInput = new ConfigInput(j);
		
	}
	
	private void initProcess(JSONArray j) {
		configProcess = new ConfigProcess(j);
		
	}

	private void initOutput(JSONObject j) {
		configOutput = new ConfigOutput(j);
		
	}

}
