package com.skp.logmetric.output.kafka;

import org.json.JSONObject;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigOutputKafka implements ConfigItem {
	String type;
	String broker;
	String topic;

/*	public ConfigOutputKafka(JSONObject j) {
		init(j);
	} */

/*	@Override
	public void init(JSONObject j) {
		type = (String) j.get("type");
		broker = (String) j.get("broker");
		topic = (String) j.get("topic");
	} */

	@Override
	public void prepare() {

	}

}
