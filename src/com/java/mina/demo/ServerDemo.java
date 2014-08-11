package com.java.mina.demo;

import com.java.mina.core.server.Server;

public class ServerDemo {
	
	public static void main(String[] args) {
		int bufferSize = 2048;
		new Server(bufferSize).start();
	}

}
