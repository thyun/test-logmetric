package com.skp.logmetric.process;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.ConfigValue;
import com.skp.logmetric.datastore.MetricEventDatastore;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.event.MetricEvent;
import com.skp.util.CommonHelper;

public class ProcessMetrics {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public boolean process(ConfigProcessMetrics config, LogEvent e) {
		ConfigValue cv = e.getConfigValue(config.getKey());
		String tkey = cv.getTargetField();
		String tvalue = cv.getTargetValueString();
/*		String tkey = config.getKey();		// Target key (ex) host
		String tvalue = e.getString(tkey);	// Target value (ex) 127.0.0.1 */
		Date ttimestamp = getMetricTimestamp(e.getTimestamp());	// Target timestamp
		
		logger.debug("ProcessMetrics.process(): tkey=" + tkey + ", tvalue=" + tvalue + ", ttimestamp=" + CommonHelper.timestamp2Str(ttimestamp));
		MetricEvent me = MetricEventDatastore.getInstance().getMetricEvent(tkey, tvalue, ttimestamp);
		me.sampling();
		for (String meter: config.getMeter()) {
			if (!e.has(meter)) {
				continue;
			}
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

	private Date getMetricTimestamp(Date timestamp) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(timestamp);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private String getLastKey(String tkey, Date ttimestamp) {
		return tkey + "-" + CommonHelper.timestamp2Str(ttimestamp);
	}

	private String getLastValue(String tvalue, Date ttimestamp) {
		return tvalue + "-" + CommonHelper.timestamp2Str(ttimestamp);
	}

}
