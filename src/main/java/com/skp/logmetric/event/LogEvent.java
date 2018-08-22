package com.skp.logmetric.event;

import java.util.Date;

import org.json.JSONObject;

import com.skp.util.CommonHelper;

import lombok.Data;

@Data
public class LogEvent extends JSONObject {
	String key;
	Date timestamp;
	
	public static LogEvent parse(String key, String value) {
		LogEvent e = new LogEvent(key, value);
		return e;
	}

	public LogEvent(String key) {
		super();
		this.key = key;
		
		this.timestamp = new Date();
		this.put("@timestamp", CommonHelper.timestamp2Str(timestamp));
	}
	
	public LogEvent(String key, String value) {
		super(value);
		this.key = key;
		
		this.timestamp = new Date();
		this.put("@timestamp", CommonHelper.timestamp2Str(timestamp));
	}

}
