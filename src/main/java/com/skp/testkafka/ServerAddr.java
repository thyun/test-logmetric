package com.skp.testkafka;

public class ServerAddr {
	String host;
	int port;
	public ServerAddr(String server) {
		String arr[] = server.split(":");
		host = arr[0];
		port = Integer.parseInt(arr[1]);
	}
	public String getHost() {
		return host;
	}
	public Integer getPort() {
		return port;
	}
}
