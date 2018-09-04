package com.skp.logmetric.datastore;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.skp.logmetric.event.MetricEvent;
import com.skp.util.CommonHelper;

import lombok.Data;

@Data
public class MetricEventDatastore {
	static MetricEventDatastore instance = null;
	
	ConcurrentHashMap<String, MetricEvent> hashMap = new ConcurrentHashMap<>();
	public static MetricEventDatastore getInstance() {
		if (instance == null) {
			instance = new MetricEventDatastore();
		}
		return instance;
	}
	
	public synchronized MetricEvent getMetricEvent(String tkey, String tvalue, Date ttimestamp) {
		String lkey = getLastKey(tkey, ttimestamp);		// Last key (ex) host-8387583535
		String lvalue = getLastValue(tvalue, ttimestamp);	// Last value (ex) 127.0.0.1-8387583535
		
		MetricEvent me = hashMap.get(lvalue);
		if (me == null) {
			me = new MetricEvent(lkey);
			me.put(tkey, tvalue);
			me.setTimestamp(ttimestamp);
			
			hashMap.put(lvalue, me);
		}
		
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
