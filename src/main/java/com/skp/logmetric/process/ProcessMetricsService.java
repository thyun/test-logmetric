package com.skp.logmetric.process;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.skp.logmetric.datastore.MetricEventDatastore;
import com.skp.logmetric.event.MetricEvent;

@Service
public class ProcessMetricsService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	int EXPORT_DURATION = 90;		// sec

	@Scheduled(cron="0 * * * * ?")
    public void export(){
		export(EXPORT_DURATION);
    }

	public void export(int duration) {
//      logger.debug("MetricEventDatastore: " + MetricEventDatastore.getInstance().toString());
      ConcurrentHashMap<String, MetricEvent> hashMap = MetricEventDatastore.getInstance().getHashMap();
      hashMap.forEach((key, me) -> {
      	if (me.afterCreateTime(duration)) {
      		logger.debug("Process metrics output: " + me.toString());
      		hashMap.remove(key);
      	}
      });
		
	}

}
