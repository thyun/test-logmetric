package com.skp.logmetric.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.skp.logmetric.event.KeyValue;
import com.skp.util.ResourceHelper;

import lombok.Data;

@Data
public class ConfigRegex {
	public final static String DELIMITER = "=";
	private static ConfigRegex instance = null;
	Map<String, String> map;
	
	public static ConfigRegex getInstance() {
		if (instance == null) {
			instance = create(ResourceHelper.getResourceLineList("regex.conf"));
		}
		return instance;
	}

	public static ConfigRegex create(List<String> lineList) {
		Map map = lineList.stream()
		.filter(line -> {
			return line.indexOf(DELIMITER) > 0;
		})
		.map(line -> {
			int index = line.indexOf(DELIMITER);
			String key, value;
			key = line.substring(0, index).trim();
			value = line.substring(index).trim();
			return new KeyValue(key, value);
		}).collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
		return new ConfigRegex(map);
	}
	
	public ConfigRegex(Map map) {
		this.map = map;
	}
	
//	public static RegexConfig create() {
//		return new RegexConfig();
//	}
	
	public void add(String key, String value) {
		map.put(key, value);
	}


}
