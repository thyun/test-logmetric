package com.skp.logmetric.output;

import com.skp.logmetric.input.InputPlugin;

import lombok.Data;

@Data
public class OutputKafka implements OutputPlugin {
	OutputQueue outputQueue;

	public OutputKafka(ConfigOutputKafka config) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
