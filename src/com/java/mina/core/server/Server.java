package com.java.mina.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.core.filter.MyCharsetCodecFactory;

public class Server extends Thread {
	
	public static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static Integer BUFFER_SIZE;
	
	public static Integer SESSION_COUNT;
	
	public Server(Integer bufferSize) {
		BUFFER_SIZE = bufferSize;
		SESSION_COUNT = 0;
	}
	
	public void server() throws IOException {
		IoAcceptor acceptor = new NioSocketAcceptor();
		// 日志过滤器
//		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		// 编码解码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory(Constant.CHARSET)));
		// 多线程处理过滤器
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(
				Executors.newCachedThreadPool()));
		acceptor.setHandler(new ServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(BUFFER_SIZE);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); // 10s
		
		acceptor.bind(new InetSocketAddress(Constant.TEXT_PORT)); // throw an IOException
		acceptor.bind(new InetSocketAddress(Constant.IMAGE_PORT)); // throw an IOException
		System.out.println("text port " + Constant.TEXT_PORT + " is listening...");
		System.out.println("image port " + Constant.IMAGE_PORT + " is listening...");
	}
	
	@Override
	public void run() {
		try {
			server();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
