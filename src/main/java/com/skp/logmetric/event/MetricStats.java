package com.skp.logmetric.event;

import java.util.HashMap;

import org.json.JSONObject;

import lombok.Data;

@Data
public class MetricStats {
	public final static String DELIMITER = ".";

	String meter;
	Long lsum = new Long(0);
	Long lmin = Long.MAX_VALUE;
	Long lmax = Long.MIN_VALUE;
	Double dsum = new Double(0);
	Double dmin = Double.MAX_VALUE;
	Double dmax = Double.MIN_VALUE;
	HashMap<String, ValueStats> valueStatsHashMap = new HashMap<>();

	public MetricStats(String meter) {
		this.meter = meter;
	}

	public void apply(Long o) {
		lsum += o;
		if (o < lmin)
			lmin = o;
		if (o > lmax)
			lmax = o;
		
		ValueStats valueStats = getValueStats(o.toString());
		valueStats.apply();
	}
	
	public void apply(Double o) {
		dsum += o;
		if (o < dmin)
			dmin = o;
		if (o > lmax)
			dmax = o;
		
		ValueStats valueStats = getValueStats(o.toString());
		valueStats.apply();
	}

	private ValueStats getValueStats(String key) {
		ValueStats valueStats = valueStatsHashMap.get(key);
		if (valueStats == null) {
			valueStats = new ValueStats(key);
			valueStatsHashMap.put(key, valueStats);
		}
		return valueStats;
	}

	public void export(JSONObject j) {
		j.put(meter + DELIMITER + "sum", lsum);
		j.put(meter + DELIMITER + "min", lmin);
		j.put(meter + DELIMITER + "max", lmax);
		for (ValueStats vs: valueStatsHashMap.values()) {
			vs.export(meter, j);
		}
	}

}
