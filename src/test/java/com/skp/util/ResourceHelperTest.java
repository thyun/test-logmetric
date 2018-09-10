package com.skp.util;

import java.io.IOException;

import org.junit.Test;

import com.skp.util.ResourceHelper;
import com.skp.util.ResourceHelper.LineReadCallback;

public class ResourceHelperTest {
	
	@Test
	public void testGetResource() throws IOException {
		String log = ResourceHelper.getResourceString("access.log");
		System.out.println("access.log=" + log);
	}
	
	@Test
	public void testProcessResource() throws IOException {
		ResourceHelper.processResource("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				System.out.println("line=" + line);
			} 
		});
	}
}
