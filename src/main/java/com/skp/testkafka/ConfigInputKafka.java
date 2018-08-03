package com.skp.testkafka;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="input.kafka")
public class ConfigInputKafka {
	public String zookeeper;
	public String broker;
	public String topics;
	public String groupId;
}
