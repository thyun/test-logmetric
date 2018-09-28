package com.skp.logmetric.event;

import java.util.HashMap;

import org.json.JSONObject;

public class MetricMeterRangeStats {
	String field;
	long unit;
	Object firstValue;
	HashMap<String, MetricValueStats> metricValueStatsHashMap = new HashMap<>();

	public MetricMeterRangeStats(String field, long unit, Object value) {
		this.field = field;
		this.unit = unit;
		this.firstValue = value;
	}

	public void applyMeterRange(Long o) {
		String range = getRangeValue(o);
		MetricValueStats valueStats = getMetricValueStats(range.toString());
		valueStats.apply();		
	}
	
	private String getRangeValue(Long o) {
		long r = o / unit;
		return "r_" + (r * unit);
	}

	public void applyMeterRange(Double o) {
		String range = getRangeValue(o);
		MetricValueStats valueStats = getMetricValueStats(range.toString());
		valueStats.apply();
	}
	
	private String getRangeValue(Double o) {
		long r = o.longValue() / unit;
		return "r_" + (r * unit);
	}

	public void applyMeterRange(String o) {
		// Not defined yet
	}
	
	private MetricValueStats getMetricValueStats(String key) {
		MetricValueStats valueStats = metricValueStatsHashMap.get(key);
		if (valueStats == null) {
			valueStats = new MetricValueStats(key);
			metricValueStatsHashMap.put(key, valueStats);
		}
		return valueStats;
	}

	public void export(JSONObject j) {
		for (MetricValueStats vs: metricValueStatsHashMap.values()) {
			vs.export(field, j);
		}
	}

}
