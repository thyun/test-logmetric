package com.skp.logmetric.event;

import java.util.Date;

import org.json.JSONObject;

import com.skp.util.CommonHelper;

import lombok.Data;

@Data
public class LogEvent extends JSONObject {
//	String key;
	Date timestamp;
	
	public static LogEvent parse(String value) {
		LogEvent e = new LogEvent(value);
		return e;
	}

	public LogEvent() {
		super();
//		this.key = key;
		
		this.timestamp = new Date();
		this.put("@timestamp", CommonHelper.timestamp2Str(timestamp));
	}
	
	public LogEvent(String value) {
		super(value);
//		this.key = key;
		
		setTimestamp(new Date());
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
		this.put("@timestamp", CommonHelper.timestamp2Str(timestamp));
	}

	public void setTimestamp(Date timestamp, String tfield) {
		this.timestamp = timestamp;
		this.put(tfield, CommonHelper.timestamp2Str(timestamp));
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LogEvent " + super.toString());
		return sb.toString();
	}

}
