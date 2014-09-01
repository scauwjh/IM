package com.java.im.core.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.client.vo.ClientUtil;
import com.java.im.core.filter.ClientKeepAliveMessageFactory;
import com.java.im.core.filter.GlobalCharsetCodecFactory;
import com.java.im.util.Debug;

public class Client {

	public static final Logger logger = LoggerFactory.getLogger(Client.class);

	public static SocketConnector connector;

	public static IoSession textSession;

	public static IoSession imageSession;

	private static ConnectFuture textFuture;

	private static ConnectFuture imageFuture;

	private ClientUtil util;

	public Client() {
		// init connector
		connector = new NioSocketConnector(Runtime.getRuntime()
				.availableProcessors());
		connector.getSessionConfig().setUseReadOperation(true);
		connector.setConnectTimeoutMillis(Constant.CONNECT_OVERTIME);
		
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new GlobalCharsetCodecFactory()));
		// 多线程处理过滤器
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(
				Executors.newCachedThreadPool()));

		// add heart beat filter
		ClientKeepAliveMessageFactory ckamf = new ClientKeepAliveMessageFactory();
		KeepAliveFilter hbFilter = new KeepAliveFilter(ckamf, IdleStatus.READER_IDLE,
				 KeepAliveRequestTimeoutHandler.CLOSE);
		hbFilter.setForwardEvent(true);
		hbFilter.setRequestInterval(Constant.CLIENT_HEARTBEAT_INTERVAL);
		hbFilter.setRequestTimeout(Constant.HEARTBEAT_TIMEOUT);
		connector.getFilterChain().addLast("heartbeat", hbFilter);

		
		connector.setHandler(new ClientHandler() {
			@Override
			public void messageReceived(IoSession session, Object message)
					throws Exception {
				logger.info("message received from: "
						+ session.getRemoteAddress());
				messageHandler(message);
			}

			@Override
			public void sessionClosed(IoSession session) throws Exception {
				logger.warn("lost connection from server");
				closeSession(session);
			}
		}); // add handler
		
		connect(-1);
		
		Debug.println("connect to remote host: " + Constant.SERVER_HOST);
		Debug.println("text port: " + Constant.TEXT_PORT);
		Debug.println("image port: " + Constant.IMAGE_PORT);

		// im api init
		util = new ClientUtil();
	}
	
	/**
	 * port: <= 0 is all
	 * @param type
	 */
	public static void connect(Integer port) {
		if (port == Constant.TEXT_PORT || port <= 0) {
			// connect to image port
			textFuture = connector.connect(new InetSocketAddress(
					Constant.SERVER_HOST, Constant.TEXT_PORT));
			textFuture.awaitUninterruptibly();
			textSession = textFuture.getSession();
			textSession.setAttribute(Constant.SESSION_PORT, Constant.TEXT_PORT);
		}
		if (port == Constant.IMAGE_PORT || port <= 0) {
			// connect to image port
			imageFuture = connector.connect(new InetSocketAddress(
					Constant.SERVER_HOST, Constant.IMAGE_PORT));
			imageFuture.awaitUninterruptibly();
			imageSession = imageFuture.getSession();
			imageSession.setAttribute(Constant.SESSION_PORT, Constant.IMAGE_PORT);
		}
	}

	/**
	 * login
	 * @param user
	 * @param accessToken
	 * @return
	 */
	public Boolean login(String user, String accessToken) {
		if (!util.login(textSession, user, accessToken))
			return false;
		return util.login(imageSession, user, accessToken);
	}
	
	/**
	 * login
	 * @param user
	 * @param accessToken
	 * @return
	 */
	public Boolean login(IoSession session, String user, String accessToken) {
		if (!util.login(session, user, accessToken)) {
			Integer port = (Integer) session.getAttribute(Constant.SESSION_PORT);
			Client.connect(port);
			return login(user, accessToken);
		}
		return true;
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
		return util.sendMessage(textSession, sender, receiver, accessToken,
				params, message);
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
		return util.sendImage(imageSession, sender, receiver, accessToken,
				params, filePath);
	}

	// ---------------------------------------
	// methods for overriding
	// ---------------------------------------
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
