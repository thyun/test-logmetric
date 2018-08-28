package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class ConfigProcessDate extends ConfigProcessItem {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String type;
	String field;
	String pattern;
	String target;

	public ConfigProcessDate(JSONObject j) {
		init(j);
	}

	private void init(JSONObject j) {		
		type = (String) j.get("type");
		field = (String) j.get("field");
		pattern = (String) j.get("pattern");
		target = (String) j.get("target");
	}

	public void prepare() {
	}

}
