package com.skp.logmetric.event;

import java.util.HashMap;

import org.json.JSONObject;

import lombok.Data;

@Data
public class MetricFieldStats {
	public final static String DELIMITER = ".";

	String meter;
	Object firstValue;
	Long lsum = new Long(0);
	Long lmin = Long.MAX_VALUE;
	Long lmax = Long.MIN_VALUE;
	Double dsum = new Double(0);
	Double dmin = Double.MAX_VALUE;
	Double dmax = Double.MIN_VALUE;
	HashMap<String, MetricValueStats> metricValueStatsHashMap = new HashMap<>();

	public MetricFieldStats(String meter, Object firstValue) {
		this.meter = meter;
		this.firstValue = firstValue;
	}

	public void apply(Long o) {
		lsum += o;
		if (o < lmin)
			lmin = o;
		if (o > lmax)
			lmax = o;
		
		MetricValueStats valueStats = getValueStats(o.toString());
		valueStats.apply();
	}
	
	public void apply(Double o) {
		dsum += o;
		if (o < dmin)
			dmin = o;
		if (o > lmax)
			dmax = o;
		
		MetricValueStats valueStats = getValueStats(o.toString());
		valueStats.apply();
	}
	
	public void apply(String o) {
		// Not defiend yet
	}

	private MetricValueStats getValueStats(String key) {
		MetricValueStats valueStats = metricValueStatsHashMap.get(key);
		if (valueStats == null) {
			valueStats = new MetricValueStats(key);
			metricValueStatsHashMap.put(key, valueStats);
		}
		return valueStats;
	}

	public void export(JSONObject j) {
		if (firstValue instanceof Long) {
			j.put(meter + DELIMITER + "sum", lsum);
			j.put(meter + DELIMITER + "min", lmin);
			j.put(meter + DELIMITER + "max", lmax);
		} else if (firstValue instanceof Double) {
			j.put(meter + DELIMITER + "sum", dsum);
			j.put(meter + DELIMITER + "min", dmin);
			j.put(meter + DELIMITER + "max", dmax);
		} else if (firstValue instanceof String) {
			// Not defined yet
		}
		for (MetricValueStats vs: metricValueStatsHashMap.values()) {
			vs.export(meter, j);
		}
	}

}
