package com.skp.logmetric.event;

import java.util.HashMap;

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
		// TODO Auto-generated method stub
		
	}

	public void stats(String meter, String o) {
		MetricStats ms = getMetricStats(meter);
		// TODO Auto-generated method stub
		
	}

	private MetricStats getMetricStats(String meter) {
		MetricStats ms = metricStatsHashMap.get(meter);
		if (ms == null) {
			ms = new MetricStats(meter);
			metricStatsHashMap.put(meter, ms);
		}
		return ms;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("MetricEvent");
		sb.append(" key=" + this.getKey());
		sb.append(" sampling=" + this.getSampling());
		for (MetricStats ms: metricStatsHashMap.values()) {
			sb.append(" ");
			sb.append(ms.toString());
		}
		return sb.toString();
	}

}
