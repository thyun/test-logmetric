package com.skp.logmetric.datastore;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.MetricEvent;
import com.skp.util.CommonHelper;

import lombok.Data;

@Data
public class MetricEventDatastore {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static MetricEventDatastore instance = null;
	
	ConcurrentHashMap<String, MetricEvent> hashMap = new ConcurrentHashMap<>();
	public static MetricEventDatastore getInstance() {
		if (instance == null) {
			instance = new MetricEventDatastore();
		}
		return instance;
	}
	
	public synchronized MetricEvent getMetricEvent(String tkey, String tvalue, Date ttimestamp) {
		String lkey = getLastKey(tkey, ttimestamp);		// Last key (ex) host-2018-12-06T05:57:00.000Z
		String lvalue = getLastValue(tvalue, ttimestamp);	// Last value (ex) 127.0.0.1-2018-12-06T05:57:00.000Z
		
		MetricEvent me = hashMap.get(lvalue);
		if (me == null) {
			logger.debug("getMetricEvent(): " + "New MetricEvent");

			me = new MetricEvent(lkey);
			me.put(tkey, tvalue);
			me.setTimestamp(ttimestamp);
			
			hashMap.put(lvalue, me);
		} else
			logger.debug("getMetricEvent(): " + "Existing MetricEvent");
		
		return me;
	}
	
/*	public MetricEvent getMetricEvent(String lkey, String lvalue) {
		MetricEvent me = hashMap.get(lvalue);
		if (me == null) {
			me = new MetricEvent(lkey, lvalue);
			hashMap.put(lvalue, me);
		}
		return me;
	} */
	
	private String getLastKey(String tkey, Date ttimestamp) {
		return tkey + "-" + CommonHelper.timestamp2Str(ttimestamp);
	}

	private String getLastValue(String tvalue, Date ttimestamp) {
		return tvalue + "-" + CommonHelper.timestamp2Str(ttimestamp);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (MetricEvent me : hashMap.values()) {
			sb.append("\n");
			sb.append(me.export());
//			sb.append(me.toString());
		}
		return sb.toString();
	}

}
