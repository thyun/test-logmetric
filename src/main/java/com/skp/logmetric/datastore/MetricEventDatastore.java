package com.skp.logmetric.datastore;

import java.util.HashMap;

import org.json.JSONObject;

import com.skp.logmetric.event.MetricEvent;

public class MetricEventDatastore {
	static MetricEventDatastore instance = null;
	
	HashMap<String, MetricEvent> hashMap = new HashMap<>();
	public static MetricEventDatastore getInstance() {
		if (instance == null) {
			instance = new MetricEventDatastore();
		}
		return instance;
	}
	
	public MetricEvent getMetric(String key) {
		MetricEvent me = hashMap.get(key);
		if (me == null) {
			me = new MetricEvent(key, new JSONObject());
			hashMap.put(key, me);
		}
		return me;
	}
	
	public String toString() {
		return hashMap.toString();
	}

}
