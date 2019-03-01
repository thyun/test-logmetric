package com.skp.logmetric.output.kafka;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigOutputKafka implements ConfigItem {
	String type;
	String broker;
	String topic;

	@Override
	public void prepare() {

	}

}
