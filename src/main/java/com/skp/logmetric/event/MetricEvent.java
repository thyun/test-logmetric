package com.skp.logmetric.event;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import lombok.Data;

@Data
public class MetricEvent extends LogEvent {
	int sampling;
	HashMap<String, MetricStats> metricStatsHashMap = new HashMap<>();
	
	public MetricEvent(String tkey, String tvalue) {
		super(tvalue);
		this.put(tkey, tvalue);
	}

	public void sampling() {
		sampling++;
	}

	public void stats(String meter, Long o) {
		MetricStats ms = getMetricStats(meter);
		ms.apply(o);
	}

	public void stats(String meter, Double o) {
		MetricStats ms = getMetricStats(meter);
		ms.apply(o);
		
	}

	public void stats(String meter, String o) {
		MetricStats ms = getMetricStats(meter);		
	}

	private MetricStats getMetricStats(String meter) {
		MetricStats ms = metricStatsHashMap.get(meter);
		if (ms == null) {
			ms = new MetricStats(meter);
			metricStatsHashMap.put(meter, ms);
		}
		return ms;
	}
	
	public String export() {
		JSONObject j = new JSONObject();
		j.put("sampling", this.getSampling());
		
		Iterator<String> it = this.keys();
		while (it.hasNext()) {
			String key = it.next();
			j.put(key, this.get(key));
		}
		
		for (MetricStats ms: metricStatsHashMap.values()) {
			ms.export(j);
//			sb.append(" ");
//			sb.append(ms.toString());
		}
		
		return j.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("MetricEvent");
		sb.append(" key=" + this.getKey());
		sb.append(" sampling=" + this.getSampling());
		
		Iterator<String> it = this.keys();
		while (it.hasNext()) {
			String key = it.next();
			sb.append(" " + key + "=" + this.getString(key));
		}
		
		for (MetricStats ms: metricStatsHashMap.values()) {
			sb.append(" ");
			sb.append(ms.toString());
		}
		return sb.toString();
	}

}
