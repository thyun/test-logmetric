package com.skp.logmetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/
 */
public class MainKafkaNewTopicConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MainKafkaNewTopicConsumer.class);
	
//    private  ExecutorService executor;
    
    public static void main(String[] args) {
    	logger.info("start");
    	
    	if (args.length < 4) {
    		printHelp();
    		System.exit(1);
    	}
        String broker = args[0];
        String groupId = args[1];
        String topic = args[2];
        int numConsumers = Integer.parseInt(args[3]);
        
        List<String> topics = Arrays.asList(topic);		// Arrays.asList("foo", "bar");
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<GeneralConsumer> consumerList = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
        	GeneralConsumer consumer = GeneralConsumer.createConsumer(i, broker, groupId);
//        	KafkaConsumer<String, String> kafkaConsumer = GeneralConsumer.createKafkaConsumer(broker, groupId);
//            GeneralConsumer consumer = new GeneralConsumer(i, kafkaConsumer);
            consumerList.add(consumer);
        	
            consumer.subscribe(topics);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
          @Override
          public void run() {
            for (GeneralConsumer consumer : consumerList) {
              consumer.shutdown();
            } 
            executor.shutdown();
            try {
              executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        });
     
    }
    
    private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("java -jar kafka-new-consumer.jar {broker} {groupId} {topic} {numConsumer}");
		System.out.println("ex) java -jar kafka-new-consumer.jar localhost:9092 mygroup access_log 3");
	}
    
}
