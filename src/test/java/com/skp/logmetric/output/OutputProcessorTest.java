package com.skp.logmetric.output;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.generator.OutputQueueGenerator;

public class OutputProcessorTest {
//	private static final Logger logger = LoggerFactory.getLogger(OutputProcessorTest.class);
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testOutputFile() throws IOException, ParseException, InterruptedException {
		// Get config
//		String input = FileHelper.getFile("process-nxlog.conf");
		Config config = Config.createFromResource("process-nxlog.conf", "regex.conf");
		
	    // Create OutputProcessor
	    OutputProcessor oprocess = new OutputProcessor(config);
	    oprocess.init();
	    
	    // Get OutputFile
	    OutputFile outputFile = (OutputFile) oprocess.getOutputPluginList().get(0);
	    
	    // Generate sample data
	    OutputQueueGenerator.generateSampleCnxlogJson("access.log", outputFile.getOutputQueue());
//	    generateSampleJson(outputFile.getOutputQueue());
	    
	    // Process
	    outputFile.process();
	}

	/*
	static long offset;
	public static void generateSampleJson(OutputQueue outputQueue) {
	    offset = 0;
		FileHelper.processFileFromResource("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				try {
					List<LogEvent> elist1 = createLogEventList(produceJson("web01", line));
					List<LogEvent> elist2 = createLogEventList(produceJson("web02", line));
					outputQueue.put(elist1);
					outputQueue.put(elist2);
				} catch (InterruptedException e) {
					logger.error(e.toString());
				}
			}
		});
	}
	
	protected static List<LogEvent> createLogEventList(String value) {
		ArrayList<LogEvent> elist = new ArrayList<>();
		elist.add(LogEvent.parse(value));
		return elist;
	}

	private static String produceJson(String host, String line) {
		JSONObject j = new JSONObject();
		j.put("hostname", host);
		j.put("nxtime", 1536298656382L);
		j.put("logInstance", "Anvil");
		j.put("sourceType", "pmon-accesslog");
		j.put("log",  line);
		return j.toString();
	} */

}
