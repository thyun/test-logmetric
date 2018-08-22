package com.skp.logmetric.event;

import lombok.Data;

@Data
public class ValueStats {
	long count=0;

	public void apply() {
		count++;
	}

}
