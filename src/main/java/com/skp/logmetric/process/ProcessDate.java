package com.skp.logmetric.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;

public class ProcessDate {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public boolean process(ConfigProcessDate config, LogEvent e) {
		String field = config.getField();
		String target = config.getTarget();
		SimpleDateFormat fmt = config.getPatternFmt();
		try {
			String value = e.getString(field);
			Date timestamp = fmt.parse(value);
			e.setTimestamp(timestamp, target);
		} catch (JSONException | ParseException ex) {
			logger.error("Error", ex);
			return false;
		}
		logger.debug("Process date output: " + e.toString());
		return true;
	}

}
