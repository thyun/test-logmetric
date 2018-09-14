package com.skp.logmetric.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.process.ProcessQueue;

import lombok.Data;

@Data
public class OutputFile implements OutputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ConfigOutputFile config;
	ExecutorService executor;
	OutputQueue outputQueue = new OutputQueue();
	BufferedWriter writer;

	public OutputFile(ConfigOutputFile config) {
		this.config = config;
	}

	@Override
	public void run() {
		while (true)
			process();
		
	}

	public void process() {
		try {
			List<LogEvent> elist = outputQueue.take();
			for (LogEvent e: elist)
				process(config, e);
			writer.flush();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void process(ConfigOutputFile config, LogEvent e) {
		logger.debug("Output file: " + e);
		try {
			writer.write(e.toString());
			writer.write("\n");
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	@Override
	public void init() {
        int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        
        try {
			writer = new BufferedWriter(new FileWriter(config.getPath(), true));
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	@Override
	public void start() {
		executor.submit(this);
		
	}

	@Override
	public void stop() {
		executor.shutdown();
	}

}
