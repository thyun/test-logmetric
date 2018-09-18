package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.skp.logmetric.input.kafka.ConfigInputKafka;
import com.skp.logmetric.output.ConfigOutputFile;
import com.skp.logmetric.output.kafka.ConfigOutputKafka;

import lombok.Data;

@Data
public class ConfigOutput {
	JSONObject j;
	List<ConfigItem> configOutputList = new ArrayList<ConfigItem>();
//	String type;
//	String path;

	public ConfigOutput(JSONObject j) {
		super();
		this.j = j;
		init();
	}

	private void init() {
//		type = (String) j.get("type");
//		path = (String) j.get("path");
		ConfigItem item = createConfigOutputPlugin(j);
		if (item != null)
			configOutputList.add(item);
	}

	private ConfigItem createConfigOutputPlugin(JSONObject j) {
		String type = (String) j.get("type");
		if ("file".equals(type)) {
			return new ConfigOutputFile(j);
		} else if ("kafka".equals(type)) {
			return new ConfigOutputKafka(j);
		}
		return null;
	}

	public void prepare() {
		
	}
}
