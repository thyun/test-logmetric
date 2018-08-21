package com.skp.testutil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommonHelper {
	static SimpleDateFormat timestampFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static String timestamp2Str(Date timestamp) {
		timestampFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return timestampFmt.format(timestamp);
	}

}
