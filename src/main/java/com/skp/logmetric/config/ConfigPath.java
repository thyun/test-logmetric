package com.skp.logmetric.config;

public class ConfigPath {
	static String processConf = "process.conf";
	static String regexConf = "regex.conf";
	
	public static String getProcessConf() {
		return processConf;
	}
	
	public static String getRegexConf() {
		return regexConf;
	}
	
	public static void setProcessConf(String value) {
		processConf = value;
	}
	
	public static void setRegexConf(String value) {
		regexConf = value;
	}
}
