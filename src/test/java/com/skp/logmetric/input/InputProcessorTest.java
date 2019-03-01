package com.skp.logmetric.input;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import com.skp.logmetric.GeneralConsumerTest;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.input.kafka.GeneralConsumer;
import com.skp.logmetric.input.kafka.InputKafka;
import com.skp.logmetric.process.ProcessQueue;
import com.skp.util.StreamFileHelper;

public class InputProcessorTest {
//	private static final Logger logger = LoggerFactory.getLogger(InputProcessorTest.class);
	static 	    long offset = 0;
	
	@Before
	public void setUp() {
		ProcessQueue.getInstance().clear();
	}
	
	@Test
	public void testInputKafka() throws IOException, ParseException, InterruptedException {
		// Get config
		Config config = Config.createFromResource("process-nxlog.conf", "regex.conf");
		
	    // Create InputProcessor
	    InputProcessor iprocess = new InputProcessor(config);
	    iprocess.init();
	    
	    // Get InputKafka & GeneralConsumer
	    InputKafka inputKafka = (InputKafka) iprocess.getInputPluginList().get(0);	// Assume 1 input plugin
	    GeneralConsumer gconsumer = inputKafka.getConsumerList().get(0);		// Assume 1 GeneralConsumer
	    String topic = inputKafka.getConfig().getTopic();
	    
	    // Apply MockConsumer 
	    MockConsumer<String, String> mockConsumer = createMockConsumer();
	    gconsumer.applyMockConsumer(mockConsumer, topic);
	    
	    // Generate sample data
	    StreamFileHelper.getFileFromResource("access.log")
		.forEach(line -> {
			mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0,	offset++, "mykey", GeneralConsumerTest.produceJson("web01", line)));
			mockConsumer.addRecord(new ConsumerRecord<String, String>(topic, 0,	offset++, "mykey", GeneralConsumerTest.produceJson("web02", line)));
		});
//	    GeneralConsumerTest.generateSampleJson(mockConsumer, topic);
	    
	    // Consume
	    gconsumer.consume();
	    List<LogEvent> elist = ProcessQueue.getInstance().take();
	    assertEquals(200, elist.size());
	}

	private MockConsumer<String, String> createMockConsumer() {
		MockConsumer<String, String> mockConsumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
	    return mockConsumer;
	}

}
