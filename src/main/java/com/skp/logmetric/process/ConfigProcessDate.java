package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.ConfigItem;

import lombok.Data;

@Data
public class ConfigProcessDate implements ConfigItem {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String type;
	String field;
	String pattern;
	String target;

	public ConfigProcessDate(JSONObject j) {
		init(j);
	}

	public void init(JSONObject j) {		
		type = (String) j.get("type");
		field = (String) j.get("field");
		pattern = (String) j.get("pattern");
		target = (String) j.get("target");
	}

	public void prepare() {
	}

}
