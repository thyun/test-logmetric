package com.skp.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;

public class CommonHelper {
	static SimpleDateFormat timestampFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static {
		timestampFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	// SimpleDateFormat is not thread safe
	public synchronized static String timestamp2Str(Date timestamp) {
        return timestampFmt.format(timestamp);
	}
	
	public static String exception2Str(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}

	public static List<String> jsonarray2StringList(JSONArray jsonArray) {
		ArrayList<String> r = new ArrayList<>();
		if (jsonArray == null)
			return r;
		for (int i=0; i<jsonArray.length(); i++) {
			r.add(jsonArray.getString(i));
		}
		return r;
	}

}
