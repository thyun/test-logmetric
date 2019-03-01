package com.skp.logmetric.generator;

import java.util.ArrayList;
import java.util.List;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.output.OutputQueue;
import com.skp.util.StreamFileHelper;

public class OutputQueueGenerator {
//	private static final Logger logger = LoggerFactory.getLogger(OutputQueueGenerator.class);

	static long offset;
	public static void generateSampleCnxlogJson(String path, OutputQueue outputQueue) {
		offset = 0;
		StreamFileHelper.getFileFromResource(path)
		.forEach(line -> {
			generateLogEventList(line, outputQueue);
		});
	}

	private static void generateLogEventList(String line, OutputQueue outputQueue) {
		List<LogEvent> elist = new ArrayList<>();

		// Make LogEvent list
		elist.add(new LogEvent(SampleJsonGenerator.produceCnxlogJson("web01", line)));

		// Put to queue
		try {
			outputQueue.put(elist);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

/*		// Make LogEvent list
		JSONArray ja = new JSONArray(line);
		for (int i=0; i<ja.length(); i ++) {
			JSONObject jo = ja.getJSONObject(i);
			elist.add(new LogEvent(jo.toString()));
		}

		// Put to queue
		try {
			ProcessQueue.getInstance().put(elist);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
	}

}
