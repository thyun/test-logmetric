package com.skp.logmetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/
 */
public class MainKafkaNewPartitionConsumerOld {
	private static final Logger logger = LoggerFactory.getLogger(MainKafkaNewPartitionConsumerOld.class);
	
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
        int partition = Integer.parseInt(args[3]);
        int numConsumers = 1;
        
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<PartitionConsumerOld> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
          PartitionConsumerOld consumer = new PartitionConsumerOld(i, broker, groupId, topic, partition);
          consumers.add(consumer);
          executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
          @Override
          public void run() {
            for (PartitionConsumerOld consumer : consumers) {
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
		System.out.println("java -jar kafka-new-partition-consumer.jar {broker} {groupId} {topic} {partition}");
		System.out.println("ex) java -jar kafka-new-partition-consumer.jar localhost:9092 mygroup access_log 3");
	}
    
}
