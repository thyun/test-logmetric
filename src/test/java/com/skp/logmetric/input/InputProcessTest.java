package com.skp.logmetric.input;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.skp.logmetric.config.Config;
import com.skp.util.ResourceHelper;

public class InputProcessTest {
	@Test
	public void testInputProcess() throws IOException, ParseException, InterruptedException {
		// Get config
		String input = ResourceHelper.getResourceString("process.conf");
		Config config = Config.create(input);
		
	    // Setup InputProcess
	    InputProcess process = new InputProcess(config);
	    process.init();
//	    process.process();
	    
	    Thread.sleep(5000);
	}

}
