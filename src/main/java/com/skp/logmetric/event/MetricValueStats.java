package com.skp.logmetric.event;

import org.json.JSONObject;

import lombok.Data;

@Data
public class MetricValueStats {
	String key;
	long count=0;
	
	public MetricValueStats(String key) {
		this.key = key;
	}

	public void apply() {
		count++;
	}

	// "responseCode.200.count" : 10
	public void export(String meter, JSONObject j) {
		j.put(meter + MetricFieldStats.DELIMITER + key, count);
		
	}

}
