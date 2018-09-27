package com.skp.logmetric.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.skp.logmetric.event.KeyValue;
import com.skp.util.FileHelper;

import lombok.Getter;

@Getter
public class ConfigRegex extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	public final static String DELIMITER = "=";

	public static ConfigRegex create() {
		return create(FileHelper.getFileLineListFromPath(ConfigPath.regexConf));
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
	
	static String VALUE_VARIABLE_REGEX = "%\\{(\\S+?)}";
	public String getValueWithRaw(String raw) {
		Pattern p = Pattern.compile(VALUE_VARIABLE_REGEX);
		Matcher m = p.matcher(raw);
		if (m.find()) {
			String key = m.group(1);
			return get(key);
		}
		return raw;
	}
	
	public String getValue(String key) {
		return get(key);
	}
	
	public String toString() {
		return super.toString();
	}


}
