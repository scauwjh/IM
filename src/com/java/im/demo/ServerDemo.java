package com.java.im.demo;

import com.java.im.core.server.Server;

public class ServerDemo extends Thread {

	@Override
	public void run() {
		Server server = new Server();
		server.startServer();
	}

	public static void main(String[] args) {
		 ServerDemo run = new ServerDemo();
		 run.start();
	}
}
