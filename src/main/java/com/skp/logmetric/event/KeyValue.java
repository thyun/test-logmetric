package com.skp.logmetric.event;

import lombok.Data;

@Data
public class KeyValue {
	String key;
	String value;
	
	public KeyValue(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

}
