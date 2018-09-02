package com.skp.logmetric.input.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class GeneralConsumer08 implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
    
    public interface ConsumerCallback08 {
    	void consume(int id, ConsumerRecords<String, String> records);
    }
 
    public GeneralConsumer08(KafkaStream a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext())
            System.out.println("Thread " + m_threadNumber + " (tid=" + Thread.currentThread().getId() + "): " + new String(it.next().message()));
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
