package com.skp.logmetric.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigVariable {
	
	static String VALUE_VARIABLE_REGEX = "%\\{(\\S+?)}";
	public static String getValueWithRaw(String raw) {
		Pattern p = Pattern.compile(VALUE_VARIABLE_REGEX);
		Matcher m = p.matcher(raw);
		if (m.find()) {
			String key = m.group(1);
			return ConfigRegex.getInstance().get(key);
		}
		return raw;
	}
	
	public static String getValue(String key) {
		return ConfigRegex.getInstance().get(key);
	}

}
