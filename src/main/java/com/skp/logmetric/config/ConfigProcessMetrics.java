package com.skp.logmetric.config;

import org.json.JSONObject;

import lombok.Data;

@Data
public class ConfigProcessMetrics extends ConfigProcessItem {
	JSONObject j;
	String type;
	String key;

	public ConfigProcessMetrics(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
		type = (String) j.get("type");
		key = (String) j.get("key");
	}

	public void prepare() {
		
	}
}
