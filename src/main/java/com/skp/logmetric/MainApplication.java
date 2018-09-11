package com.skp.logmetric;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigPath;
import com.skp.logmetric.input.InputProcessor;
import com.skp.logmetric.output.OutputProcessor;
import com.skp.logmetric.process.ProcessMetricsService;
import com.skp.logmetric.process.ProcessProcessor;
import com.skp.util.ResourceHelper;

@SpringBootApplication
public class MainApplication {
	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);
	ApplicationContext ctx;
	Config config;
	InputProcessor iprocessor;
	ProcessProcessor pprocessor;
	OutputProcessor oprocessor;
		
	public static void main(String[] args) {        
		logger.info("Start");
		MainApplication main = new MainApplication();
		main.start(args);
	}

	private void start(String[] args) {
		if (args.length > 1)
			ConfigPath.setProcessConf(args[0]);
		if (args.length > 2)
			ConfigPath.setRegexConf(args[1]);
		logger.debug("processConf=" + ConfigPath.getProcessConf());
		logger.debug("regexConf=" + ConfigPath.getRegexConf());
		
		// Get spring context
		ctx = SpringApplication.run(MainApplication.class, args);
		printSpringBeans();
		
		// Get config
		String input = ResourceHelper.getResourceString(ConfigPath.getProcessConf());
		config = Config.create(input);
		
		// Start
		startOutput();
		startProcess();
		startInput();
		
		// Support Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	iprocessor.stop();
            	pprocessor.stop();
            	oprocessor.stop();
            }
          });
	}

	private void printHelp() {
		System.out.println("Usage:");
		System.out.println("java -jar logmetric.jar {process.conf} {regex.conf}");
	}

	private void startInput() {
		iprocessor = new InputProcessor(config);
	    iprocessor.init();
		iprocessor.start();
	}

	private void startProcess() {
		// Create ProcessProcessor
	    pprocessor = new ProcessProcessor(config);
	    pprocessor.init();
		pprocessor.start();
		pprocessor.setOutputProcessor(oprocessor);
		
		// Set ProcessMetricsService
		ProcessMetricsService pms = (ProcessMetricsService) ctx.getBean("processMetricsService");
		pms.setOutputProcessor(oprocessor);
	}
	
	private void startOutput() {
		// Create OutputProcessor
		oprocessor = new OutputProcessor(config);
		oprocessor.init();
		oprocessor.start();
	}
	
	private void printSpringBeans() {        
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
	}

}
