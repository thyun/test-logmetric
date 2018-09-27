package com.skp.util;

import java.io.IOException;

import org.junit.Test;

import com.skp.util.FileHelper;
import com.skp.util.FileHelper.LineReadCallback;

public class ResourceHelperTest {
	
	@Test
	public void testGetFileFromResource() throws IOException {
		String log = FileHelper.getFileFromResource("access.log");
		System.out.println("access.log=" + log);
	}
	
	@Test
	public void testProcessFileFromResource() throws IOException {
		FileHelper.processFileFromResource("access.log", new LineReadCallback() {
			@Override
			public void processLine(String line) {
				System.out.println("line=" + line);
			} 
		});
	}
}
