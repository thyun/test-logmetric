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
	String foo;
	
	public static Config create(String value) throws ParseException {
		Config config = new Config();
		config.init(value);
		config.prepare();
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
