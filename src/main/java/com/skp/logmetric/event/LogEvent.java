package com.skp.logmetric.event;

import java.util.Date;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;

import com.skp.testutil.CommonHelper;

import lombok.Data;

@Data
public class LogEvent {
	JSONObject j;
	Date timestamp;
	
	public static LogEvent parse(ConsumerRecord<String, String> record) {
		JSONObject j = new JSONObject(record.value());
		LogEvent e = new LogEvent(j);
		return e;
	}

	public LogEvent(JSONObject j) {
		this.j = j;
		
		this.timestamp = new Date();
		this.j.put("@timestamp", CommonHelper.timestamp2Str(timestamp));
	}

}
