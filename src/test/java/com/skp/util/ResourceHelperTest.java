package com.skp.util;

import java.io.IOException;

import org.junit.Test;

import com.skp.util.FileHelper;
import com.skp.util.FileHelper.LineReadCallback;

public class ResourceHelperTest {
	
	@Test
	public void testGetResource() throws IOException {
		String log = FileHelper.getFile("access.log");
		System.out.println("access.log=" + log);
	}
	
	@Test
	public void testProcessResource() throws IOException {
		FileHelper.processFile("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				System.out.println("line=" + line);
			} 
		});
	}
}
