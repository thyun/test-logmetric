package com.skp.logmetric.input.kafka;

import java.util.Arrays;
import java.util.List;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class GeneralConsumer08 implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
    private ConsumerCallback08 callback = null;
    
    public interface ConsumerCallback08 {
    	void consume(int id, List<String> records);
    }
 
    public GeneralConsumer08(KafkaStream a_stream, int a_threadNumber, ConsumerCallback08 callback) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
        this.callback = callback;
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext()) {
        	consume(it.next());
 //           System.out.println("Thread " + m_threadNumber + " (tid=" + Thread.currentThread().getId() + "): " + new String(it.next().message()));
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }

	private void consume(MessageAndMetadata<byte[], byte[]> mm) {
		String message = new String(mm.message());
		String records[] = message.split("\n");
		List<String> recordList = Arrays.asList(records);
		if (callback != null)
			callback.consume(m_threadNumber, recordList);
	}
}
