package com.java.mina.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.java.mina.core.filter.MyCharsetCodecFactory;

public class MINAServer extends Thread {
	
	public static Integer PORT;
	
	public static Integer BUFFER_SIZE;
	
	public static Integer SESSION_COUNT;
	
	public MINAServer(Integer port, Integer bufferSize) {
		PORT = port;
		BUFFER_SIZE = bufferSize;
		SESSION_COUNT = 0;
	}
	
	public void server() throws IOException {
		IoAcceptor acceptor = new NioSocketAcceptor();
		// 日志过滤器
//		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		// 编码解码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory()));
		// 多线程处理过滤器
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(
				Executors.newCachedThreadPool()));
		acceptor.setHandler(new ServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(BUFFER_SIZE);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); // 10s
		acceptor.bind(new InetSocketAddress(PORT)); // throw an IOException
		System.out.println("port " + PORT + " is listening...");
	}
	
	@Override
	public void run() {
		try {
			server();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new MINAServer(9999, 2048).start();
	}
}
