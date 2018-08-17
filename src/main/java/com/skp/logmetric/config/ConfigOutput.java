package com.skp.logmetric.config;

import org.json.JSONObject;

import lombok.Data;

@Data
public class ConfigOutput {
	JSONObject j;
	String type;
	String path;

	public ConfigOutput(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
		type = (String) j.get("type");
		path = (String) j.get("path");
	}

	public void prepare() {
		
	}
}
