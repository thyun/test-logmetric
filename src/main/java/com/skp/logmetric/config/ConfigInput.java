package com.skp.logmetric.config;

import org.json.JSONObject;

import lombok.Data;

@Data
public class ConfigInput {
	JSONObject j;
	String type;
	String topic;

	public ConfigInput(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
		type = (String) j.get("type");
		topic = (String) j.get("topic");
	}

	public void prepare() {
		
	}
	

}
