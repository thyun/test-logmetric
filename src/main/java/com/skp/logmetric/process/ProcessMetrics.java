package com.skp.logmetric.process;

import com.skp.logmetric.datastore.MetricEventDatastore;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.event.MetricEvent;

public class ProcessMetrics {
	
	public boolean process(ConfigProcessMetrics config, LogEvent e) {
		String tkey = config.getKey();
		String tvalue = e.getString(tkey);
		
		MetricEvent me = MetricEventDatastore.getInstance().getMetricEvent(tkey, tvalue);
		me.sampling();
		me.setTimestamp(e.getTimestamp());
		for (String meter: config.getMeter()) {
			Object o = e.get(meter);
			if (o instanceof Long)
				me.stats(meter, (Long) o);
			else if (o instanceof Double)
				me.stats(meter, (Double) o);
			else
				me.stats(meter, (String) o);
		}
		return true;
	}

}
