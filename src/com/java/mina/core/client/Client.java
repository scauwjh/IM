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
	
	protected SocketConnector connector;
	
	protected ConnectFuture textFuture;
	
	protected ConnectFuture imageFuture;
	
	protected IoSession textSession;
	
	protected IoSession imageSession;
	
	protected  API api;
	
	
	public Client() { 
		// init connector
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(Constant.CONNECT_TIMEOUT); // set connect timeout
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory(Constant.CHARSET))); // add charset filter
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
		
		// im api init
		api = new APIInstance();
	}
	
	/**
	 * login
	 * @param user
	 * @param password
	 */
	public void login(String user, String password) {
		api.login(textSession, user, password);
	}
	
	/**
	 * <p>init image session</p>
	 * <p>set image session login status</p>
	 * @param user
	 * @param password
	 */
	public void initImageSession(String user, String password) {
		api.login(imageSession, user, password);
	}
	
	/**
	 * send message
	 * @param sender
	 * @param receiver
	 * @param message
	 */
	public void sendMessage(String sender, String receiver, String message) {
		api.sendMessage(textSession, sender, receiver, message);
	}
	
	/**
	 * send heartbeat
	 * @param account
	 */
	public void sendHeartbeat(String account) {
		api.sendHeartbeat(textSession, account);
	}
	
	/**
	 * send image
	 * @param sender
	 * @param receiver
	 * @param filePath
	 */
	public void sendImage(String sender, String receiver, String filePath) {
		api.sendImage(imageSession, sender, receiver, filePath);
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
