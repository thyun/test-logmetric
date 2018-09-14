package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;

import lombok.Data;
import lombok.Getter;

@Data
public class ConfigValue {
	private final static Logger logger = LoggerFactory.getLogger(ConfigValue.class);
	
	String raw;
	String targetField;
	Object targetValue;
	
	public ConfigValue(String raw, String targetField, Object targetValue) {
		this.raw = raw;
		this.targetField = targetField;
		this.targetValue = targetValue;
	}

	static String VALUE_VARIABLE_PREFIX = "%";
	public static ConfigValue create(LogEvent e, String raw) {
		ConfigValue cv = null;
		if (raw.startsWith(VALUE_VARIABLE_PREFIX)) {
			cv = createWithVariable(e, raw);
			
		} else
			cv = new ConfigValue(raw, raw, makeTargetValue(e, raw));
		
		return cv;
	}
	
	static String VALUE_VARIABLE_INNER = "[";
	private static ConfigValue createWithVariable(LogEvent e, String raw) {
		String inner = getInner(raw);
		logger.debug("innter=" + inner);
		List<String> fields;
		if (inner.startsWith(VALUE_VARIABLE_INNER)) {
			fields = getFields(inner);
		} else
			fields = Arrays.asList(inner);
		return new ConfigValue(raw, makeTargetField(fields), makeTargetValue(e, fields));
	}

	// (\\S+?) - Match field (? means non-greedy)
	public static String FIELDS_REGEX = "\\[(\\S+?)\\]";
	private static List<String> getFields(String inner) {
		Pattern p = Pattern.compile(FIELDS_REGEX);
		Matcher m = p.matcher(inner);
		ArrayList<String> fields = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		int count=0;
		while(m.find()) {
	         count++;
	         logger.debug("Match number "+count);
	         logger.debug("groupCount=" + m.groupCount());
	         logger.debug("group 1=" + m.group(1));
	         fields.add(m.group(1));
	    }
		return fields;
	}

	private static String makeTargetField(List<String> fields) {
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i<fields.size(); i++) {
			String field = fields.get(i);
			sb.append(field);
			if (i < fields.size()-1 )
				sb.append(".");
		}
		return sb.toString();
	}

	private static Object makeTargetValue(LogEvent e, List<String> fields) {
		JSONObject j = (JSONObject) e;
		Object o = new String("N/A");
		for (int i=0; i<fields.size(); i++) {
			String field = fields.get(i);
			if (i < fields.size()-1) {
				if (j.has(field))
					j = j.getJSONObject(field);
				else
					return o;
			} else
				o = j.get(field);
		}
		return o;
	}

	private static Object makeTargetValue(LogEvent e, String field) {
		if (e.has(field))
			return e.get(field);
		return ConfigRegex.getInstance().getValue(field);
	}

	static String VALUE_VARIABLE_REGEX = "%\\{(\\S+?)}";
	public static String getInner(String variable) {
		Pattern p = Pattern.compile(VALUE_VARIABLE_REGEX);
		Matcher m = p.matcher(variable);
		if (m.find()) {
			String key = m.group(1);
			return key;
		}
		return variable;
	}
	
	public String getTargetValueString() {
		return "" + targetValue;
	}

}
