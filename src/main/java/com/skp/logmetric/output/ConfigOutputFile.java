package com.skp.logmetric.output;

import org.json.JSONObject;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigOutputFile implements ConfigItem {
	String type;
	String path;
	
	public ConfigOutputFile(JSONObject j) {
		init(j);
	}

	@Override
	public void init(JSONObject j) {
		type = (String) j.get("type");
		path = (String) j.get("path");
//		max = (String) j.get("max");

	}

	@Override
	public void prepare() {

	}

}
