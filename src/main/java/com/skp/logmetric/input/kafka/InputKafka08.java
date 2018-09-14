package com.skp.logmetric.input.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.InputPlugin;
import com.skp.logmetric.input.kafka.GeneralConsumer08.ConsumerCallback08;
import com.skp.logmetric.process.ProcessQueue;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import lombok.Data;

@Data
public class InputKafka08 implements InputPlugin {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	ConfigInputKafka08 config;
	List<GeneralConsumer08> consumerList = new ArrayList<>();
	private ConsumerConnector consumerConnector;
    ExecutorService executor;

	public InputKafka08(ConfigInputKafka08 config) {
		this.config = config;
	}
	
	public void init() {
        int numConsumers = 1;
        executor = Executors.newFixedThreadPool(numConsumers);
        
        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(config.getZookeeper(), config.getGroup()));
        
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(config.getTopic(), new Integer(numConsumers));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(config.getTopic());
        
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
        	GeneralConsumer08 consumer = new GeneralConsumer08(stream, threadNumber, new ConsumerCallback08() {
				@Override
				public void consume(int id, List<String> records) {
					process(id, records);
				}
        		
        	});
            consumerList.add(consumer);
            threadNumber++;
        }
		
	}

	public void start() {
		for (GeneralConsumer08 consumer: consumerList)
			executor.submit(consumer);
	}
	
	public void stop() {
        if (consumerConnector != null) 
        	consumerConnector.shutdown();
        if (executor != null) 
        	executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
	}
	
	private void process(int id, List<String> records) {
		try {
			List<LogEvent> elist = createLogEventList(records);
			ProcessQueue.getInstance().put(elist);
			for (LogEvent e: elist) 
				logger.debug("Input kafka08 " + e);
		} catch (InterruptedException e) {
			logger.error(e.toString());
		}

/*		for (String record : records) {
			logger.debug("Consumer " + id + ": " + ", value=" + record);
			try {
				ProcessQueue.getInstance().put(createLogEvent(record));
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
		} */
	}

	private List<LogEvent> createLogEventList(List<String> records) {
		ArrayList<LogEvent> elist = new ArrayList<>();
		for (String record: records) {
			elist.add(LogEvent.parse(record));
		}
		return elist;
	}

/*	private LogEvent createLogEvent(String value) {
		return LogEvent.parse(value);
	} */

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "3000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
//        props.put("zookeeper.session.timeout.ms", "400");
//        props.put("zookeeper.sync.time.ms", "200");
//        props.put("auto.commit.interval.ms", "1000");
 
        return new ConsumerConfig(props);
    }

	@Override
	public void run() {
	}

}
