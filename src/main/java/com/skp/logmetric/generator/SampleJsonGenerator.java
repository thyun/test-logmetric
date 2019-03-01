package com.skp.logmetric.generator;

import org.json.JSONArray;
import org.json.JSONObject;

public class SampleJsonGenerator {
	
	public static String produceCnxlogJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("hostname", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	}
	
	public static String produceFilebeatJson(String host, String line) {
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
	}

}
