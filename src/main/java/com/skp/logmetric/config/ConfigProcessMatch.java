package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class ConfigProcessMatch extends ConfigProcessItem {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

//	JSONObject j;
	String type;
	String field;
	String pattern;
	String patternRegex;
	ArrayList<TypeField> typeFieldList = new ArrayList<>();

	public ConfigProcessMatch(JSONObject j) {
//		this.j = j;
		init(j);
	}

	// TODO Read pattern from file
	private void init(JSONObject j) {
		type = (String) j.get("type");
		field = (String) j.get("field");
		pattern = "%{WORD:ip} %{WORD:identd} %{WORD:userid} \\[%{DATE:date}\\] \"%{WORD} %{WORD:request} %{WORD}\" %{LONG:responseCode} %{LONG:byteSent} \"%{DATA:referer}\" \"%{DATA:client}\" \"%{DOUBLE:responseTime}\"(?:$|\\s.*)";		
	}

	public void prepare() {
		preparePattern();
	}
	
	private void preparePattern() {
		Pattern p = Pattern.compile(TypeField.MATCH_REGEX);
		Matcher m = p.matcher(pattern);
		StringBuffer sb = new StringBuffer();
		int count=0;
		while(m.find()) {
	         count++;
	         logger.debug("Match number "+count);
	         logger.debug("groupCount=" + m.groupCount());
	         logger.debug("group 0=" + m.group(0) + ", start=" + m.start() + ", end=" + m.end());
	         logger.debug("group 1=" + m.group(1));
	         logger.debug("group 2=" + m.group(2));
	         String typeRegex = TypeField.getTypeRegex(m.group(1));
	         m.appendReplacement(sb, Matcher.quoteReplacement(typeRegex));
	         TypeField tf = TypeField.create(count, m.group(1), m.group(2));
	         typeFieldList.add(tf);
	    }
		m.appendTail(sb);
		patternRegex = sb.toString();
	}

}
