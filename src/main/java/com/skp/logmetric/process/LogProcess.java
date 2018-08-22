package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skp.logmetric.ConsumerTest;
import com.skp.logmetric.config.Config;
import com.skp.logmetric.config.ConfigProcess;
import com.skp.logmetric.config.ConfigProcessItem;
import com.skp.logmetric.config.ConfigProcessMatch;
import com.skp.logmetric.config.ConfigProcessMetrics;
import com.skp.logmetric.config.TypeField;
import com.skp.logmetric.datastore.MetricEventDatastore;
import com.skp.logmetric.event.LogEvent;
import com.skp.logmetric.event.MetricEvent;

public class LogProcess implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final int id;
	private final Consumer<String, String> consumer;
	private final Config config;
	
	public LogProcess(int id, Consumer<String, String> consumer, Config config) {
		this.id = id;
		this.consumer = consumer;
		this.config = config;
	}
	
	public void subscribe(List<String> topics) {
		consumer.subscribe(topics);
	}
	
	public void assign(String topic, List<Integer> partitions) {
		List<TopicPartition> topicPartitions;
		
		topicPartitions = new ArrayList<>();
		for (Integer partition : partitions) {
			topicPartitions.add(new TopicPartition(topic, partition));
		}
	    consumer.assign(topicPartitions);
	}

	@Override
	public void run() {
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				process(records);
			}
		} catch (WakeupException e) {
			// ignore for shutdown 
		} finally {
			consumer.close();
		}
	}

	public void shutdown() {
		consumer.wakeup();
	}
	
	// Used for test
	public void consume() {
		ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
		process(records);
		logger.debug("MetricEventDatastore: " + MetricEventDatastore.getInstance().toString());
	}

	private void process(ConsumerRecords<String, String> records) {
		for (ConsumerRecord<String, String> record : records) {
			System.out.println("Consumer " + this.id + ": " + "partition=" + record.partition() + ", offset=" + record.offset() + ", value=" + record.value());
			try {
				LogEvent e = LogEvent.parse(record.key(), record.value());
				process(config, e);
			} catch (JSONException e) {
				logger.error("LogConsumer.process() exception: " + e);
				e.printStackTrace();
			}
		}
	}
	
	private void process(Config config, LogEvent e) {
		
		ConfigProcess configProcess = config.getConfigProcess();
		List<ConfigProcessItem> configProcessList = configProcess.getConfigProcessList();
		for (ConfigProcessItem item : configProcessList) {
			boolean r=true;
			if (item instanceof ConfigProcessMatch)
				r = processMatch((ConfigProcessMatch) item, e);
			
			if (item instanceof ConfigProcessMetrics)
				r = processMetrics((ConfigProcessMetrics) item, e);
			
			if (r != true)
				return;
		}
		
	}

	private boolean processMatch(ConfigProcessMatch config, LogEvent e) {
		String tfield = config.getField();
		String tvalue = e.getString(tfield);
		e.remove(tfield);
		
		String regex = config.getPatternRegex();
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(tvalue);
		if (!m.find()) {
			logger.error("Process match fail: target value=" + tvalue);
			return false;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("Process match success:");
		List<TypeField> typeFieldList = config.getTypeFieldList();
		for (TypeField tf : typeFieldList) {
			int pos = tf.getPos();
			String type = tf.getType();
			String value = m.group(pos);
			if (tf.getField() != null) {
				if (TypeField.KEY_LONG.equals(type))
					e.put(tf.getField(), Long.parseLong(value));
				else if (TypeField.KEY_DOUBLE.equals(type))
					e.put(tf.getField(), Double.parseDouble(value));
				else
					e.put(tf.getField(), value);
			}
			sb.append(" " + pos + "=" + m.group(pos));
		}
		logger.debug(sb.toString());
		logger.debug("Process match output:" + e.toString());
		return true;
	}
	
	private boolean processMetrics(ConfigProcessMetrics config, LogEvent e) {
		String tkey = config.getKey();
		String tvalue = e.getString(tkey);
		
		MetricEvent me = MetricEventDatastore.getInstance().getMetricEvent(tkey, tvalue);
		me.sampling();
		for (String meter: config.getMeter()) {
			Object o = e.get(meter);
			if (o instanceof Long)
				me.stats(meter, (Long) o);
			else if (o instanceof Double)
				me.stats(meter, (Double) o);
			else
				me.stats(meter, (String) o);
		}
		return true;
	}

}
