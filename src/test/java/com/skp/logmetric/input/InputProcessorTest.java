package com.skp.logmetric.input;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.GeneralConsumer;
import com.skp.logmetric.GeneralConsumerTest;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.input.kafka.InputKafka;
import com.skp.logmetric.process.ProcessQueue;
import com.skp.util.ResourceHelper;

public class InputProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(InputProcessorTest.class);
//	MockConsumer<String, String> kafkaConsumer;
//	String topic = "my_topic";
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testInputProcess() throws IOException, ParseException, InterruptedException {
		// Get config
		String input = ResourceHelper.getResourceString("process.conf");
		Config config = Config.create(input);
		
	    // Get InputProcess
	    InputProcessor iprocess = new InputProcessor(config);
	    iprocess.init();
	    
	    // Get InputKafka
	    InputKafka inputKafka = (InputKafka) iprocess.getInputPluginList().get(0);	// Assume 1 input plugin
	    String topic = inputKafka.getConfig().getTopic();
	    
	    // Apply MockConsumer
	    MockConsumer<String, String> mockConsumer = createMockConsumer(topic);
	    GeneralConsumer gconsumer = inputKafka.getConsumerList().get(0);		// Assume 1 GeneralConsumer
	    gconsumer.setKafkaConsumer(mockConsumer);
	    gconsumer.subscribe(Arrays.asList(topic));
	    mockConsumer.rebalance(Collections.singletonList(new TopicPartition(topic, 0)));
	    mockConsumer.seek(new TopicPartition(topic, 0), 0);
	    
	    // Consume InputKafka
	    GeneralConsumerTest.generateSampleJson(mockConsumer, topic);
	    gconsumer.consume();
	    assertEquals(200, ProcessQueue.getInstance().size());
	}

	private MockConsumer<String, String> createMockConsumer(String topic) {
		MockConsumer<String, String> mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
	    
	    // Set topic offset
	    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
	    beginningOffsets.put(new TopicPartition(topic, 0), 0L);
	    mockConsumer.updateBeginningOffsets(beginningOffsets);
	    
	    return mockConsumer;
	}

}
