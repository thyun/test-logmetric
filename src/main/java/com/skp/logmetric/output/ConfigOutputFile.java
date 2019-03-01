package com.skp.logmetric.output;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigOutputFile implements ConfigItem {
	String type;
	String path;
	long max;

	@Override
	public void prepare() {

	}

}
