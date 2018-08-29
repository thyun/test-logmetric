package com.skp.logmetric.input;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigInput;
import com.skp.logmetric.config.ConfigItem;
import com.skp.logmetric.input.kafka.ConfigInputKafka;
import com.skp.logmetric.input.kafka.InputKafka;

public class InputProcess {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Config config;
	ArrayList<InputPlugin> inputPluginList = new ArrayList<>();
	
	public InputProcess(Config config) {
		this.config = config;
	}
	
	public void init() {
		addPlugin();
		int count = inputPluginList.size();
		ExecutorService executor = Executors.newFixedThreadPool(count);
		for (InputPlugin ip: inputPluginList) {
			executor.submit(ip);
		}
	}

	private void addPlugin() {
		ConfigInput configInput = config.getConfigInput();
		for (ConfigItem cp: configInput.getConfigInputList()) {
			if (cp instanceof ConfigInputKafka) {
				inputPluginList.add(new InputKafka((ConfigInputKafka) cp));
			}
		}
	}

}
