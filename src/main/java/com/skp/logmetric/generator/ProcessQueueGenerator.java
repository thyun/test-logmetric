package com.skp.logmetric.generator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.process.ProcessQueue;
import com.skp.util.StreamFileHelper;

public class ProcessQueueGenerator {
//	private static final Logger logger = LoggerFactory.getLogger(ProcessQueueGenerator.class);

	static long offset;
	public static void generateSampleMetric(String path) {
		offset = 0;
		StreamFileHelper.getFileFromResource(path)
		.forEach(line -> {
			generateSampleMetricEventList(line);
		});
	}
	
	// TODO JSONArray parsing 후 다시 String 변환하여 LogEvent 생성하므로 비효율적
	private static void generateSampleMetricEventList(String line) {
		List<LogEvent> elist = new ArrayList<>();

		// Make LogEvent list
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
		}
	}
	
	public static void generateSampleCnxlogJson(String path) {
		offset = 0;
		StreamFileHelper.getFileFromResource(path)
		.forEach(line -> {
			generateSampleCnxlogJsonEventList(line);
		});
	}

	private static void generateSampleCnxlogJsonEventList(String line) {
		List<LogEvent> elist1 = new ArrayList<>();
		List<LogEvent> elist2 = new ArrayList<>();

		// Make LogEvent list
		elist1.add(new LogEvent(SampleJsonGenerator.produceCnxlogJson("web01", line)));
		elist1.add(new LogEvent(SampleJsonGenerator.produceCnxlogJson("web02", line)));

		// Put to queue
		try {
			ProcessQueue.getInstance().put(elist1);
			ProcessQueue.getInstance().put(elist2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

/*	private static String produceCnxlogJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("hostname", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	} */
	
	public static void generateSampleFilebeatJson(String path) {
		offset = 0;
		StreamFileHelper.getFileFromResource(path)
		.forEach(line -> {
			generateSampleFilebeatJsonEventList(line);
		});
	}

	private static void generateSampleFilebeatJsonEventList(String line) {
		List<LogEvent> elist1 = new ArrayList<>();
		List<LogEvent> elist2 = new ArrayList<>();

		// Make LogEvent list
		elist1.add(new LogEvent(SampleJsonGenerator.produceFilebeatJson("web01", line)));
		elist2.add(new LogEvent(SampleJsonGenerator.produceFilebeatJson("web02", line)));

		// Put to queue
		try {
			ProcessQueue.getInstance().put(elist1);
			ProcessQueue.getInstance().put(elist2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
/*	private static String produceFilebeatJson(String host, String line) {
		JSONObject j = new JSONObject();
		JSONArray jtags = new JSONArray();
		jtags.put("beats_input_codec_plain_applied");
		JSONObject jbeat = new JSONObject();
		jbeat.put("version", "6.4.0");
		jbeat.put("name", host);
		jbeat.put("hostname", host);
		JSONObject jhost = new JSONObject();
		jhost.put("name", host);
		
		j.put("tags", jtags);
		j.put("beat", jbeat);
		j.put("message",  line);
		j.put("host", jhost);
		return j.toString();
	} */

}
