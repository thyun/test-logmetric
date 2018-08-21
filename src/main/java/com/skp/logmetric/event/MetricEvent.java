package com.skp.logmetric.event;

import org.json.JSONObject;

import lombok.Data;

@Data
public class MetricEvent extends LogEvent {
	String key;
	int sampling;

	public MetricEvent(String key, JSONObject j) {
		super(j);
		this.key = key;
	}

	public void sampling() {
		sampling++;
	}

}
