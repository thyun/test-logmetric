package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import lombok.Data;

@Data
public class ConfigInput {
	JSONObject j;
	List<ConfigPlugin> configInputList = new ArrayList<ConfigPlugin>();
//	String type;
//	String topic;

	public ConfigInput(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
		ConfigPlugin item = createConfigInputPlugin(j);
		if (item != null)
			configInputList.add(item);
	}

	private ConfigPlugin createConfigInputPlugin(JSONObject j) {
		String type = (String) j.get("type");
		if ("kafka".equals(j.get("type"))) {
			return new ConfigInputKafka(j);
		}
		return null;
	}

	public void prepare() {
		
	}
	

}
