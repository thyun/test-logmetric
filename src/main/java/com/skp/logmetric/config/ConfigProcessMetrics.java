package com.skp.logmetric.config;

import org.json.simple.JSONObject;

import lombok.Data;

@Data
public class ConfigProcessMetrics extends ConfigProcessItem {
	JSONObject j;
	String type;

	public ConfigProcessMetrics(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
		type = (String) j.get("type");
	}

	public void prepare() {
		
	}
}
