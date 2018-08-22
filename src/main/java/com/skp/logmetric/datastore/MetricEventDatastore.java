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
	
	public MetricEvent getMetricEvent(String tkey, String tvalue) {
		MetricEvent me = hashMap.get(tvalue);
		if (me == null) {
			me = new MetricEvent(tkey, tvalue);
			hashMap.put(tvalue, me);
		}
		return me;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (MetricEvent me : hashMap.values()) {
			sb.append("\n");
			sb.append(me.toString());
		}
		return sb.toString();
	}

}
