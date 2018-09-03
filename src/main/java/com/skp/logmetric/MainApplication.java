package com.skp.logmetric;

import java.util.Arrays;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.input.InputProcessor;
import com.skp.logmetric.process.ProcessProcessor;
import com.skp.util.ResourceHelper;

@SpringBootApplication
public class MainApplication {
	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);
	Config config;
	InputProcessor iprocessor;
	ProcessProcessor pprocessor;
		
	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MainApplication.class, args);
        
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        
		logger.info("Start");
		MainApplication main = new MainApplication();
		main.start();
	}
	

	private void start() {
		// Get config
		String input = ResourceHelper.getResourceString("process08.conf");
		config = Config.create(input);
		
		// Start
		startInput();
		startProcess();
	}

	private void startInput() {
		InputProcessor iprocess = new InputProcessor(config);
	    iprocess.init();
		iprocess.start();
	}

	private void startProcess() {
		// Create ProcessProcessor
	    ProcessProcessor pprocess = new ProcessProcessor(config);
	    pprocess.init();
		pprocess.start();
	}
}
