package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigItem;
import lombok.Data;

@Data
public class ConfigProcessMatch implements ConfigItem {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String type;
	String field;
	String pattern;
	
	String patternConverted;
	String patternRegex;
	ArrayList<TypeField> typeFieldList = new ArrayList<>();

	public void prepare() {
		patternConverted = Config.getConfigRegex().getValueWithRaw(pattern);
		
		Pattern p = Pattern.compile(TypeField.MATCH_REGEX);
		Matcher m = p.matcher(patternConverted);
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
