package com.skp.logmetric.event;

import org.json.JSONObject;

import lombok.Data;

@Data
public class ValueStats {
	String key;
	long count=0;
	
	public ValueStats(String key) {
		this.key = key;
	}

	public void apply() {
		count++;
	}

	// "responseCode.200.count" : 10
	public void export(String meter, JSONObject j) {
		j.put(meter + MetricStats.DELIMITER + key, count);
		
	}

}
