package com.skp.logmetric.input.kafka;

import org.json.JSONObject;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigInputKafka08 implements ConfigItem {
	String type;
	String zookeeper;
	String topic;
	String group;

	public ConfigInputKafka08(JSONObject j) {
		init(j);
	}

	@Override
	public void init(JSONObject j) {
		type = (String) j.get("type");
		zookeeper = (String) j.get("zookeeper");
		topic = (String) j.get("topic");
		group = (String) j.get("group");
	}

	@Override
	public void prepare() {

	}

}
