package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.skp.logmetric.input.kafka.ConfigInputKafka;
import com.skp.logmetric.input.kafka.ConfigInputKafka08;

import lombok.Data;

@Data
public class ConfigInput {
	JSONObject j;
	List<ConfigItem> configInputList = new ArrayList<ConfigItem>();

	public ConfigInput(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
		ConfigItem item = createConfigInputPlugin(j);
		if (item != null)
			configInputList.add(item);
	}

	private ConfigItem createConfigInputPlugin(JSONObject j) {
		String type = (String) j.get("type");
		if ("kafka".equals(type)) {
			return new ConfigInputKafka(j);
		} else if ("kafka08".equals(type)) {
			return new ConfigInputKafka08(j);
		}
		return null;
	}

	public void prepare() {
		
	}
	

}
