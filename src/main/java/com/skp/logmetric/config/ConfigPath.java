package com.skp.logmetric.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.util.FileHelper;

public class ConfigPath {
	private static final Logger logger = LoggerFactory.getLogger(ConfigPath.class);
	static String processConfPath = null;		// Final processConfPath
	static String regexConfPath = null;			// Final regexConfPath
	static String[] processConfPathArr = { "process.conf", "config/process.conf" };
	static String[] regexConfPathArr = { "reges.conf", "config/regex.conf" };
	
	public static String getProcessConfPath() {
		return processConfPath;
	}
	
	public static String getRegexConfPath() {
		return regexConfPath;
	}

	public static boolean lookup(String a_processConfPath, String a_regexConfPath) {
		processConfPath = lookupConf(a_processConfPath, processConfPathArr);
		if (processConfPath == null) {
			logger.error("processConfPath file not found");
			return false;
		}
		regexConfPath = lookupConf(a_regexConfPath, regexConfPathArr);
		if (regexConfPath == null) {
			logger.error("regexConfPath file not found");
			return false;
		}
		return true;
	}

	private static String lookupConf(String confPath, String[] confPathArr) {
		if (confPath != null) {
			if (FileHelper.exist(confPath))
				return confPath;
			return null;
		}
		
		for (String path: confPathArr) {
			if (FileHelper.exist(path))
				return path;
		}
		return null;
	}
}
