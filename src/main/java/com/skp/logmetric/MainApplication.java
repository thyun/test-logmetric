package com.skp.logmetric;

import java.util.Arrays;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.skp.logmetric.config.Config;

@SpringBootApplication
public class MainApplication {

	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);
		
	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MainApplication.class, args);
        
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        
		logger.info("start");
		Config config = (Config) ctx.getBean("configInputKafka");
	}
}
