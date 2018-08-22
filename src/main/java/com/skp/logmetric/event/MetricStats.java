package com.skp.logmetric.event;

import java.util.HashMap;

import lombok.Data;

@Data
public class MetricStats {
	String meter;
	Long sum = new Long(0);
	Long min = Long.MAX_VALUE;
	Long max = Long.MIN_VALUE;
	HashMap<String, ValueStats> valueStatsHashMap = new HashMap<>();

	public MetricStats(String meter) {
		this.meter = meter;
	}

	public void apply(Long o) {
		sum += o;
		if (o < min)
			min = o;
		if (o > max)
			max = o;
		
		ValueStats valueStats = getValueStats(o.toString());
		valueStats.apply();
	}

	private ValueStats getValueStats(String value) {
		ValueStats valueStats = valueStatsHashMap.get(value);
		if (valueStats == null) {
			valueStats = new ValueStats();
			valueStatsHashMap.put(value, valueStats);
		}
		return valueStats;
	}

}
