package com.skp.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;

public class CommonHelper {
	static SimpleDateFormat timestampFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static String timestamp2Str(Date timestamp) {
		timestampFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return timestampFmt.format(timestamp);
	}

	public static List<String> jsonarray2List(JSONArray jsonArray) {
		ArrayList<String> r = new ArrayList<>();
		for (int i=0; i<jsonArray.length(); i++) {
			r.add(jsonArray.getString(i));
		}
		return r;
	}

}
