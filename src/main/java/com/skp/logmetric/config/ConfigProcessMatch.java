package com.skp.logmetric.config;

import org.json.simple.JSONObject;

import lombok.Data;

@Data
public class ConfigProcessMatch extends ConfigProcessItem {
	JSONObject j;
	String type;
	String field;
	String pattern;
	String patternRegEx;

	public ConfigProcessMatch(JSONObject j) {
		this.j = j;
		init();
	}

	// TODO Read pattern
	private void init() {
		type = (String) j.get("type");
		field = (String) j.get("field");
		pattern = "%{WORD:ip} %{WORD:identd} %{WORD:userid} \\[%{DATE:date}\\] \"%{WORD} %{WORD:request} %{WORD}\" %{LONG:responseCode} %{LONG:byteSent} \"%{DATA:referer}\" \"%{DATA:client}\" \"%{DOUBLE:responseTime}\"(?:$|\\s.*)";		
		initPattern(pattern);
	}

	String typeRegEx = "%{(\\S):(\\S)}";
	private void initPattern(String pattern) {
		
	}

}
