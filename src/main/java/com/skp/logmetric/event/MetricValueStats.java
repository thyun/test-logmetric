package com.skp.logmetric.event;

import org.json.JSONObject;

import lombok.Data;

@Data
public class MetricValueStats {
	String value;
	long count=0;
	
	public MetricValueStats(String key) {
		this.value = key;
	}

	public void apply() {
		count++;
	}

	// "responseCode.200" : 10
	public void export(String field, JSONObject j) {
		j.put(field + MetricMeterStats.DELIMITER + value, count);
		
	}

}
