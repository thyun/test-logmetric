package com.skp.logmetric.output;

public interface OutputPlugin extends Runnable {
	public void init();
	public void start();
	public void stop();
	public OutputQueue getOutputQueue();
}
