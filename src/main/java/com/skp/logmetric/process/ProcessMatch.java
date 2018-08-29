package com.skp.logmetric.process;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.TypeField;
import com.skp.logmetric.event.LogEvent;

public class ProcessMatch {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean process(ConfigProcessMatch config, LogEvent e) {
		String tfield = config.getField();
		String tvalue = e.getString(tfield);
		e.remove(tfield);
		
		String regex = config.getPatternRegex();
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(tvalue);
		if (!m.find()) {
			logger.error("Process match fail: target value=" + tvalue);
			return false;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("Process match success:");
		List<TypeField> typeFieldList = config.getTypeFieldList();
		for (TypeField tf : typeFieldList) {
			int pos = tf.getPos();
			String type = tf.getType();
			String value = m.group(pos);
			if (tf.getField() != null) {
				if (TypeField.KEY_LONG.equals(type))
					e.put(tf.getField(), Long.parseLong(value));
				else if (TypeField.KEY_DOUBLE.equals(type))
					e.put(tf.getField(), Double.parseDouble(value));
				else
					e.put(tf.getField(), value);
			}
			sb.append(" " + pos + "=" + m.group(pos));
		}
		logger.debug(sb.toString());
		logger.debug("Process match output:" + e.toString());
		return true;
	}

}
