package com.skp.logmetric.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.output.kafka.ConfigOutputKafka;
import com.skp.logmetric.output.kafka.OutputKafka;

import lombok.Data;

@Data
public class OutputProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static ObjectMapper objectMapper = new ObjectMapper();
	
	private final Config config;
	ArrayList<OutputPlugin> outputPluginList = new ArrayList<>();
	
	public OutputProcessor(Config config) {
		this.config = config;
	}
	
	public void init() {
		addPlugin();
		for (OutputPlugin ip: outputPluginList) {
			ip.init();
		}
	}
	
	public void start() {
		for (OutputPlugin ip: outputPluginList) {
			ip.start();
		}
	}
	
	public void stop() {
		for (OutputPlugin ip: outputPluginList) {
			ip.stop();
		}
	}
	
	public void put(List<LogEvent> elist) {
		for (OutputPlugin op: outputPluginList) {
			try {
				op.getOutputQueue().put(elist);
			} catch (InterruptedException ex) {
				logger.error("Error", ex);
			}
		}
	}

	private void addPlugin() {
		HashMap<String, Object> coutput = config.getOutput();
		String type = (String) coutput.get("type");
		if (type.equals("kafka")) {
			ConfigOutputKafka coutputKafka = objectMapper.convertValue(coutput, ConfigOutputKafka.class);
			outputPluginList.add(new OutputKafka(coutputKafka));
		} else if (type.equals("file")) {
			ConfigOutputFile coutputFile = objectMapper.convertValue(coutput, ConfigOutputFile.class);
			outputPluginList.add(new OutputFile(coutputFile));
		}
		
/*		ConfigOutput configOutput = config.getConfigOutput();
		for (ConfigItem cp: configOutput.getConfigOutputList()) {
			if (cp instanceof ConfigOutputFile) {
				outputPluginList.add(new OutputFile((ConfigOutputFile) cp));
			} else if (cp instanceof ConfigOutputKafka) {
				outputPluginList.add(new OutputKafka((ConfigOutputKafka) cp));
			}
		} */
	}

}
