package com.skp.util;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonHelperTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void testException() throws IOException {
		try {
            throw new RuntimeException("Excpetion happened");
        } catch (Exception e) {
//            logger.error("Error: {}", e.getMessage(), e);
            logger.error(e.getMessage(), e);
        }
	}
	
}
