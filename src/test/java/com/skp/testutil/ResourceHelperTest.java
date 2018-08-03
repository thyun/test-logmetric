package com.skp.testutil;

import java.io.IOException;

import org.junit.Test;

import com.skp.testutil.ResourceHelper.LineReadCallback;

public class ResourceHelperTest {
	
	@Test
	public void testGetResource() throws IOException {
		String log = ResourceHelper.getResourceString("com/skp/testkafka/access.log");
		System.out.println("access.log=" + log);
	}
	
	@Test
	public void testProcessResource() throws IOException {
		ResourceHelper.processResource("com/skp/testkafka/access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				System.out.println("line=" + line);
			} 
		});
	}
}
