package com.skp.logmetric.output.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import kafka.producer.KeyedMessage;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class GeneralProducer08 {
	String broker;
	Producer<String, String> producer;
	ExecutorService executor;

	public GeneralProducer08(String broker) {
		super();
		this.broker = broker;
		
        Properties props = new Properties();
        props.put("metadata.broker.list", broker);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
//      props.put("request.required.acks", "1");
		
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
      
//        hostNum = Integer.parseInt(props.getProperty("host.num"));
//        hostErrorLogNum = Integer.parseInt(props.getProperty("host.error.log.num"));
//        hostDelayLogNum = Integer.parseInt(props.getProperty("host.delay.log.num"));
	}
	
	public void produce(List<KeyedMessage<String, String>> mlist) {
		producer.send(mlist);
	}
	
}
