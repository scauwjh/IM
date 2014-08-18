package com.java.mina.core.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.api.API;
import com.java.mina.api.APIInstance;
import com.java.mina.constant.Constant;
import com.java.mina.core.filter.MyCharsetCodecFactory;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;

public class Client {
	
	public static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static SocketConnector connector;
	
	public static IoSession textSession;
	
	public static IoSession imageSession;
	
	public static IoSession heartbeatSession;
	
	protected ConnectFuture textFuture;
	
	protected ConnectFuture imageFuture;
	
	protected ConnectFuture heartbeatFuture;
	
	protected  API api;
	
	
	public Client() { 
		// init connector
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(Constant.CONNECT_TIMEOUT); // set connect timeout
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory(Constant.CHARSET))); // add charset filter
		connector.getSessionConfig().setUseReadOperation(true); // set use read operation
		connector.setHandler(new ClientHandler() {
			@Override
			public void messageReceived(IoSession session, Object message)
					throws Exception {
				logger.info("message received from: " + session.getRemoteAddress());
				objectReceived(message);
			}
		}); // add handler
		
		
		// connect to image port
		textFuture = connector.connect(new InetSocketAddress(
				Constant.REMOTE_ADDRESS, Constant.TEXT_PORT));
		textFuture.awaitUninterruptibly();
		textSession = textFuture.getSession();
		
		// connect to image port
		imageFuture = connector.connect(new InetSocketAddress(
				Constant.REMOTE_ADDRESS, Constant.IMAGE_PORT));
		imageFuture.awaitUninterruptibly();
		imageSession = imageFuture.getSession();
		
		// connect to heartbeat port
		heartbeatFuture = connector.connect(new InetSocketAddress(
				Constant.REMOTE_ADDRESS, Constant.HEARTBEAT_PORT));
		heartbeatFuture.awaitUninterruptibly();
		heartbeatSession = heartbeatFuture.getSession();
		
		// im api init
		api = new APIInstance();
	}
	
	/**
	 * login
	 * @param user
	 * @param password
	 * @return
	 */
	public Boolean login(String user, String password) {
		return api.login(user, password);
	}
	
	/**
	 * send message
	 * @param sender
	 * @param receiver
	 * @param message
	 * @return
	 */
	public Boolean sendMessage(String sender, String receiver, Integer type, String message) {
		return api.sendMessage(textSession, sender, receiver, type, message);
	}
	
	/**
	 * send heartbeat
	 * @param account
	 * @return
	 */
	public Boolean sendHeartbeat(String account) {
		return api.sendHeartbeat(account);
	}
	
	/**
	 * send image
	 * @param sender
	 * @param receiver
	 * @param extra
	 * @param filePath
	 * @return
	 */
	public boolean sendImage(String sender, String receiver, String extra, String filePath) {
		return api.sendImage(imageSession, sender, receiver, extra, filePath);
	}
	
	/**
	 * <p>received a message</p>
	 * <p>override this method to write your service</p>
	 * @param message
	 */
	public void messageReceived(Message message) {
		
	}
	
	/**
	 * <p>received an image</p>
	 * <p>override this method to write your service</p>
	 * @param image
	 */
	public void imageReceived(Image image) {
		
	}
	
	/**
	 * <p>received a string</p>
	 * <p>override this method to write your service</p>
	 * @param string
	 */
	public void stringReceived(String string) {
		
	}
	
	/**
	 * message object received
	 * @param message
	 * @throws Exception
	 */
	protected void objectReceived(Object message) throws Exception {
		if (message instanceof Message)
			messageReceived((Message) message);
		else if (message instanceof Image)
			imageReceived((Image) message);
		else if (message instanceof String)
			stringReceived((String) message);
	}
}
