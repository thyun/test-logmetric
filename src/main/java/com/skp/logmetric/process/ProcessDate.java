package com.skp.logmetric.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.ConfigProcessDate;
import com.skp.logmetric.event.LogEvent;
import com.skp.util.CommonHelper;

public class ProcessDate {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// TODO Remove new SimpleDateFormat(pattern)
	public boolean process(ConfigProcessDate config, LogEvent e) {
		String field = config.getField();
		String pattern = config.getPattern();
		String target = config.getTarget();
		String value = e.getString(field);
		SimpleDateFormat fmt = new SimpleDateFormat(pattern);
		try {
			Date timestamp = fmt.parse(value);
			e.setTimestamp(timestamp, target);
		} catch (JSONException | ParseException ex) {
			logger.error(CommonHelper.exception2Str(ex));
			return false;
		}
		return true;
	}

}
