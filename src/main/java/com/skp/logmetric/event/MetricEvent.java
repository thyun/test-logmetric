package com.skp.logmetric.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.process.ConfigProcessMetrics.MeterRange;

import lombok.Getter;

@Getter
public class MetricEvent extends LogEvent {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	Date createTime;
	Date updateTime;
	int sampling;
	HashMap<String, MetricMeterStats> metricMeterStatsHashMap = new HashMap<>();
	HashMap<String, MetricMeterRangeStats> metricMeterRangeStatsHashMap = new HashMap<>();
	
	public MetricEvent(String lkey) {
		super();
		createTime = new Date();
		updateTime = createTime;
	}

	public synchronized void sampling() {
		sampling++;
		updateTime = new Date();
	}

	public void statsMeter(String meter, Long o) {
		MetricMeterStats ms = getMetricMeterStats(meter, o);
		ms.applyMeter(o);
	}

	public void statsMeter(String meter, Double o) {
		MetricMeterStats ms = getMetricMeterStats(meter, o);
		ms.applyMeter(o);
		
	}

	public void statsMeter(String meter, String o) {
		MetricMeterStats ms = getMetricMeterStats(meter, o);
		ms.applyMeter(o);
	}
	
	private MetricMeterStats getMetricMeterStats(String meter, Object value) {
		MetricMeterStats ms = metricMeterStatsHashMap.get(meter);
		if (ms == null) {
			ms = new MetricMeterStats(meter, value);
			metricMeterStatsHashMap.put(meter, ms);
		}
		return ms;
	}

	
	public void statsMeterRange(MeterRange mr, Long o) {
		getMetricMeterRangeStats(mr, o).applyMeterRange(o);
	}

	public void statsMeterRange(MeterRange mr, Double o) {
		getMetricMeterRangeStats(mr, o).applyMeterRange(o);
	}

	public void statsMeterRange(MeterRange mr, String o) {
		getMetricMeterRangeStats(mr, o).applyMeterRange(o);
	}

	private MetricMeterRangeStats getMetricMeterRangeStats(MeterRange mr, Object value) {
		String field = mr.getField();
		MetricMeterRangeStats stats = metricMeterRangeStatsHashMap.get(field);
		if (stats == null) {
			stats = new MetricMeterRangeStats(field, mr.getUnit(), value);
			metricMeterRangeStatsHashMap.put(field, stats);
		}
		return stats;
	}
	
	static String SAMPLING_COUNT = "count";
	public String export() {
		JSONObject j = new JSONObject();
		j.put(SAMPLING_COUNT, this.getSampling());
		
		Iterator<String> it = this.keys();
		while (it.hasNext()) {
			String key = it.next();
			j.put(key, this.get(key));
		}
		
		for (MetricMeterStats stats: metricMeterStatsHashMap.values())
			stats.export(j);
		for (MetricMeterRangeStats stats: metricMeterRangeStatsHashMap.values()) 
			stats.export(j);
		
		return j.toString();
	}
	
	public LogEvent toLogEvent() {
		return new LogEvent(toString(), getTimestamp());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(export());
		return sb.toString();
	}

	public boolean afterCreateTime(int secs) {
		Date current = new Date();
		
		long diff = current.getTime() - createTime.getTime();
//		logger.debug("current - createTime=" + diff);
		if (diff > secs * 1000)
			return true;
		return false;
	}

}
