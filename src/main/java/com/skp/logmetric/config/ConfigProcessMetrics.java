package com.skp.logmetric.config;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skp.util.CommonHelper;

import lombok.Data;

@Data
public class ConfigProcessMetrics implements ConfigPlugin {
	String type;
	String key;
	List<String> meter;

	public ConfigProcessMetrics(JSONObject j) {
		super();
		init(j);
	}

	public void init(JSONObject j) {
		type = (String) j.get("type");
		key = (String) j.get("key");
		meter = CommonHelper.jsonarray2List(j.getJSONArray("meter"));
	}

	public void prepare() {
		
	}
}
