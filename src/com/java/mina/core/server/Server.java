package com.java.mina.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.core.filter.MyCharsetCodecFactory;
import com.java.mina.util.PropertiesUtil;

public class Server {
	
	public static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	private IoAcceptor acceptor;
	
	
	public void server() throws IOException {
		acceptor = new NioSocketAcceptor();
		// 日志过滤器
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		// 编码解码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory(Constant.CHARSET)));
		// 多线程处理过滤器
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(
				Executors.newCachedThreadPool()));
		acceptor.setHandler(new ServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(Constant.SERVER_BUFFER_SIZE);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); // 10s
		
		acceptor.bind(new InetSocketAddress(Constant.TEXT_PORT)); // throw an IOException
		acceptor.bind(new InetSocketAddress(Constant.IMAGE_PORT)); // throw an IOException
		acceptor.bind(new InetSocketAddress(Constant.HEARTBEAT_PORT)); // throw an IOException
		System.out.println("text port " + Constant.TEXT_PORT + " is listening...");
		System.out.println("image port " + Constant.IMAGE_PORT + " is listening...");
		System.out.println("heartbeat port " + Constant.HEARTBEAT_PORT + " is listening...");
	}
	
	public void runServer() {
		try {
			server();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void stopServer() {
		acceptor.setCloseOnDeactivation(true);
		for (IoSession session : acceptor.getManagedSessions().values()) {
			session.close(true);
		}
		acceptor.unbind();
		acceptor.dispose();
	}
	
	public static void main(String[] args) throws InterruptedException {
		String path = Server.class.getResource("/").getPath() 
				+ "/configure.properties";
		Map<String, String> map = PropertiesUtil.getProperties(path);
		Constant.SERVER_HOST = map.get("serverHost");
		Constant.TEXT_PORT = Integer.valueOf(map.get("textPort"));
		Constant.IMAGE_PORT = Integer.valueOf(map.get("imagePort"));
		Constant.HEARTBEAT_PORT = Integer.valueOf(map.get("heartbeatPort"));
		Constant.SERVER_BUFFER_SIZE = Integer.valueOf(map.get("bufferSize"));
		Constant.SERVER_CACHE_SIZE = Integer.valueOf(map.get("cacheSize"));
		Server server = new Server();
		server.runServer();
	}
}
