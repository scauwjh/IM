package com.java.mina.core.client;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.core.client.vo.ClientUtil;
import com.java.mina.core.filter.MyCharsetCodecFactory;
import com.java.mina.util.Debug;
import com.java.mina.util.PropertiesUtil;

public class Client {
	
	public static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static SocketConnector connector;
	
	public static IoSession textSession;
	
	public static IoSession imageSession;
	
	public static IoSession heartbeatSession;
	
	private ConnectFuture textFuture;
	
	private ConnectFuture imageFuture;
	
	private ConnectFuture heartbeatFuture;
	
	private  ClientUtil util;
	
	
	public Client() { 
		// load configure
		loadProperties();
		// init connector
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(Constant.CONNECT_OVERTIME); // set connect timeout
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory())); // add charset filter
		connector.getSessionConfig().setUseReadOperation(true); // set use read operation
		connector.setHandler(new ClientHandler() {
			@Override
			public void messageReceived(IoSession session, Object message)
					throws Exception {
				logger.info("message received from: " + session.getRemoteAddress());
				messageHandler(message);
			}
			@Override
			public void sessionClosed(IoSession session) throws Exception {
				logger.warn("lost connection from server");
				closeSession(session);
			}
		}); // add handler
		
		
		// connect to image port
		textFuture = connector.connect(new InetSocketAddress(
				Constant.SERVER_HOST, Constant.TEXT_PORT));
		textFuture.awaitUninterruptibly();
		textSession = textFuture.getSession();
		
		// connect to image port
		imageFuture = connector.connect(new InetSocketAddress(
				Constant.SERVER_HOST, Constant.IMAGE_PORT));
		imageFuture.awaitUninterruptibly();
		imageSession = imageFuture.getSession();
		
		// connect to heartbeat port
		heartbeatFuture = connector.connect(new InetSocketAddress(
				Constant.SERVER_HOST, Constant.HEARTBEAT_PORT));
		heartbeatFuture.awaitUninterruptibly();
		heartbeatSession = heartbeatFuture.getSession();
		
		Debug.println("connect to remote host: " + Constant.SERVER_HOST);
		Debug.println("text port: " + Constant.TEXT_PORT);
		Debug.println("image port: " + Constant.IMAGE_PORT);
		Debug.println("heartbeat port: " + Constant.HEARTBEAT_PORT);
		
		// im api init
		util = new ClientUtil();
	}
	
	/**
	 * 获取配置
	 */
	private void loadProperties() {
		String path = this.getClass().getResource("/").getPath() 
				+ "/imconfigure.properties";
		Map<String, String> map = PropertiesUtil.getProperties(path);
		Constant.SERVER_HOST = map.get("serverHost");
		Constant.TEXT_PORT = Integer.valueOf(map.get("textPort"));
		Constant.IMAGE_PORT = Integer.valueOf(map.get("imagePort"));
		Constant.HEARTBEAT_PORT = Integer.valueOf(map.get("heartbeatPort"));
		Constant.SERVER_BUFFER_SIZE = Integer.valueOf(map.get("bufferSize"));
		Constant.SERVER_CACHE_SIZE = Integer.valueOf(map.get("cacheSize"));
	}
	
	/**
	 * login
	 * @param session
	 * @param user
	 * @param password
	 * @return
	 */
	public Boolean login(String user, String password) {
		if (!this.login(textSession, user, password)) {
			return false;
		} else if (!this.login(imageSession, user, password)) {
			return false;
		}
		return this.login(heartbeatSession, user, password);
	}
	
	/**
	 * login
	 * @param session
	 * @param user
	 * @param password
	 * @return
	 */
	public Boolean login(IoSession session, String user, String password) {
		return util.login(session, user, password);
	}
	
	/**
	 * send message
	 * @param sender
	 * @param receiver
	 * @param accessToken
	 * @param params
	 * @param message
	 * @return
	 */
	public Boolean sendMessage(String sender, String receiver, 
			String accessToken, String params, String message) {
		return util.sendMessage(textSession, sender, receiver, 
				accessToken, params, message);
	}
	
	/**
	 * send heartbeat
	 * @param account
	 * @param accessToken
	 * @return
	 */
	public Boolean sendHeartbeat(String account, String accessToken) {
		return util.sendHeartbeat(heartbeatSession, account,
				null, accessToken);
	}
	
	/**
	 * send image
	 * @param sender
	 * @param receiver
	 * @param accessToken
	 * @param params
	 * @param filePath
	 * @return
	 */
	public boolean sendImage(String sender, String receiver, 
			String accessToken, String params, String filePath) {
		return util.sendImage(imageSession, sender, receiver, 
				accessToken, params,filePath);
	}
	
	//---------------------------------------
	// methods for overriding
	//---------------------------------------
	/**
	 * <p>message handler</p>
	 * <p>override this method to write your service</p>
	 * @param message
	 * @throws Exception
	 */
	protected void messageHandler(Object message) throws Exception {
		
	}
	
	/**
	 * <p>session closed</p>
	 * <p>override this method to write your service</p>
	 * @param session
	 */
	public void closeSession(IoSession session) {
		
	}
}
