package com.skp.logmetric.process;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.ConfigItem;

import lombok.Getter;

@Getter
public class ConfigProcessDate implements ConfigItem {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String type;
	String field;
	String pattern;
	SimpleDateFormat patternFmt;
	String target;

	public ConfigProcessDate(JSONObject j) {
		init(j);
	}

	public void init(JSONObject j) {		
		type = (String) j.get("type");
		field = (String) j.get("field");
		pattern = (String) j.get("pattern");
		target = (String) j.get("target");
		
		patternFmt = new SimpleDateFormat(pattern, Locale.ENGLISH);
	}

	public void prepare() {
	}

}
