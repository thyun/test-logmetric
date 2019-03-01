package com.skp.logmetric.input.kafka;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigInputKafka implements ConfigItem {
	String type;
	String broker;
	String topic;
	String group;

	@Override
	public void prepare() {

	}

}
