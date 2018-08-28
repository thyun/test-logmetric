package com.skp.logmetric.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.skp.logmetric.event.KeyValue;
import com.skp.util.ResourceHelper;

import lombok.Data;

@Data
public class ConfigRegex extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	public final static String DELIMITER = "=";
	private static ConfigRegex instance = null;
	
	public static ConfigRegex getInstance() {
		if (instance == null) {
			instance = create(ResourceHelper.getResourceLineList("regex.conf"));
		}
		return instance;
	}

	public static ConfigRegex create(List<String> lineList) {
		Map<String, String> map = lineList.stream()
		.filter(line -> {
			return line.indexOf(DELIMITER) > 0;
		})
		.map(line -> {
			int index = line.indexOf(DELIMITER);
			String key, value;
			key = line.substring(0, index).trim();
			value = line.substring(index+1).trim();
			return new KeyValue(key, value);
		}).collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
		return new ConfigRegex(map);
	}
	
	public ConfigRegex(Map<String, String> map) {
		super(map);
	}
	
	public String toString() {
		return super.toString();
	}


}
