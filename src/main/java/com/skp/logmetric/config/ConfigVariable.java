package com.skp.logmetric.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigVariable {
	
	static String VALUE_VARIABLE_REGEX = "%\\{(\\S+?)}";
	public static String getValue(String variable) {
		Pattern p = Pattern.compile(VALUE_VARIABLE_REGEX);
		Matcher m = p.matcher(variable);
		if (m.find()) {
			String key = m.group(1);
			return ConfigRegex.getInstance().get(key);
		}
		return variable;
	}

}
