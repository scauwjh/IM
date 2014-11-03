package com.java.im.core.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.filter.GlobalCharsetCodecFactory;
import com.java.im.core.filter.ServerKeepAliveMessageFactory;
import com.java.im.util.PropertiesUtil;

public class Server {
	
	public static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static IoAcceptor acceptor;
	
	
	/**
	 * 获取配置
	 */
	static {
		String path = Server.class.getResource("/").getPath()
				+ "im.properties";
		File file = new File(path);
		if (file.exists()) {
			Map<String, String> map = PropertiesUtil.getProperties(path);
			Constant.SERVER_HOST = map.get("serverHost");
			Constant.TEXT_PORT = Integer.valueOf(map.get("textPort"));
			Constant.IMAGE_PORT = Integer.valueOf(map.get("imagePort"));
			Constant.SERVER_BUFFER_SIZE = Integer.valueOf(map.get("bufferSize"));
			Constant.SERVER_CACHE_SIZE = Integer.valueOf(map.get("cacheSize"));
			Constant.IS_DEBUG = map.get("isDebug").equals("true");
			logger.info("Read properties from configure file of customer");
		}
	}
	
	public void server() throws IOException {
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors());
		acceptor.getSessionConfig().setReadBufferSize(Constant.SERVER_BUFFER_SIZE);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
		// 日志过滤器
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		// 编码解码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new GlobalCharsetCodecFactory()));
		// 多线程处理过滤器
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(
				Executors.newCachedThreadPool()));
		// 心跳机制过滤器
		ServerKeepAliveMessageFactory skmf = new ServerKeepAliveMessageFactory();
		HeartbeatHandler hbHandler = new HeartbeatHandler();
		KeepAliveFilter hbFilter = new KeepAliveFilter(skmf, IdleStatus.BOTH_IDLE, hbHandler);
		hbFilter.setForwardEvent(true);
		hbFilter.setRequestInterval(Constant.SERVER_HEARTBEAT_INTERVAL);// 用于触发服务器检测客户端是否有心跳包发过来，规定时间内没有就断开
		acceptor.getFilterChain().addLast("heartbeat", hbFilter);
		
		acceptor.setHandler(new ServerHandler());
		
		acceptor.bind(new InetSocketAddress(Constant.TEXT_PORT)); // throw an IOException
		acceptor.bind(new InetSocketAddress(Constant.IMAGE_PORT)); // throw an IOException
		System.out.println("text port " + Constant.TEXT_PORT + " is listening...");
		System.out.println("image port " + Constant.IMAGE_PORT + " is listening...");
	}
	
	public void startServer() {
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
		Server server = new Server();
		server.startServer();
	}
}
