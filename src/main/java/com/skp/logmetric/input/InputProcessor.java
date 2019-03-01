package com.skp.logmetric.input;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigItem;
import com.skp.logmetric.input.kafka.ConfigInputKafka;
import com.skp.logmetric.input.kafka.InputKafka;

import lombok.Data;

@Data
public class InputProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();
	
	private final Config config;
	ArrayList<InputPlugin> inputPluginList = new ArrayList<>();
	
	public InputProcessor(Config config) {
		this.config = config;
	}
	
	public void init() {
		addPlugin();
		for (InputPlugin ip: inputPluginList) {
			ip.init();
		}
	}
	
	public void start() {
		for (InputPlugin ip: inputPluginList) {
			ip.start();
		}
	}
	
	public void stop() {
		for (InputPlugin ip: inputPluginList) {
			ip.stop();
		}
//		executor.shutdown();
	}

	private void addPlugin() {
		HashMap<String, Object> cinput = config.getInput();
		String type = (String) cinput.get("type");
		if (type.equals("kafka")) {
			ConfigInputKafka cinputKafka = objectMapper.convertValue(cinput, ConfigInputKafka.class);
			inputPluginList.add(new InputKafka(cinputKafka));
		}
	}

}
