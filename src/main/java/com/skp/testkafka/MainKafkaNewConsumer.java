package com.skp.testkafka;

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
public class MainKafkaNewConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MainKafkaNewConsumer.class);
	
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

        final List<RunnableConsumer> runnableConsumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
        	KafkaConsumer<String, String> consumer = createConsumer(broker, groupId);
            RunnableConsumer runnableConsumer = new RunnableConsumer(i, consumer);
            runnableConsumer.subscribe(topics);
            executor.submit(runnableConsumer);

            runnableConsumers.add(runnableConsumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
          @Override
          public void run() {
            for (RunnableConsumer consumer : runnableConsumers) {
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
    
    private static KafkaConsumer<String, String> createConsumer(String broker, String groupId) {
		Properties props = new Properties();
		props.put("bootstrap.servers", broker);
		props.put("group.id", groupId);
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		props.put("session.timeout.ms", "30000");
		return new KafkaConsumer<>(props);
    }
    
}
