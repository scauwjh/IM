package com.java.im.core.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.client.vo.ClientHeartbeat;
import com.java.im.core.client.vo.ClientUtil;
import com.java.im.core.filter.GlobalCharsetCodecFactory;
import com.java.im.core.model.DataPacket;
import com.java.im.util.Debug;

public class Client {

	public static final Logger logger = LoggerFactory.getLogger(Client.class);

	public static SocketConnector connector;

	public static IoSession textSession;

	public static IoSession imageSession;

	private ConnectFuture textFuture;

	private ConnectFuture imageFuture;

	private ClientUtil util;
	
	private ClientHeartbeat hearbeat;
	
	public Client() {
	
	}
	
	/**
	 * init client
	 * @param host
	 * <p>null for default host (Constant.SERVER_HOST)</p>
	 * @return
	 */
	public boolean initClient(String host) {
		try {
			connector = null;
			textSession = null;
			imageSession = null;
			if (host != null)
				Constant.SERVER_HOST = host;
			// init connector
			connector = new NioSocketConnector(Runtime.getRuntime()
					.availableProcessors());
			// use read operation
			connector.getSessionConfig().setUseReadOperation(true);
			//connect overtime
			connector.setConnectTimeoutMillis(Constant.CONNECT_OVERTIME);
//			// write overtime
//			connector.getSessionConfig().setWriteTimeout(Constant.WRITE_OVERTIME);
			// add codec filter
			connector.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(new GlobalCharsetCodecFactory()));
			// add thread poll filter
			connector.getFilterChain().addLast("threadPool",
					new ExecutorFilter(Executors.newCachedThreadPool()));
			// set handler
			connector.setHandler(new ClientHandler() {
				@Override
				public void messageReceived(IoSession session, Object message)
						throws Exception {
					logger.info("message received from: "
							+ session.getRemoteAddress());
					DataPacket packet = (DataPacket) message;
					// if message packet
					if (packet.getType().equals(Constant.TYPE_SEND)) {
						messageHandler(packet);
					}
					// if login packet
					else if (packet.getType().equals(Constant.TYPE_LOGIN)) {
						loginHandler(packet);
					}
				}

				@Override
				public void sessionClosed(IoSession session) throws Exception {
					logger.warn("lost connection from server");
					closeSession(session);
				}
			});
			// connect server
			if (!connect(-1)) {
				Debug.println(Constant.DEBUG_INFO, "connect to remote host false!");
				close();
				return false;
			}
			
			Debug.println(Constant.DEBUG_INFO, "connect to remote host: " + Constant.SERVER_HOST);
			Debug.println(Constant.DEBUG_INFO, "text port: " + Constant.TEXT_PORT);
			Debug.println(Constant.DEBUG_INFO, "image port: " + Constant.IMAGE_PORT);

			// im api init
			util = new ClientUtil();
			
			// start heartbeat thread
			hearbeat = new ClientHeartbeat();
			hearbeat.start();
			
			return true;
		} catch (Exception e) {
			logger.error("Failed to init client!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * port: <= 0 is all
	 * 
	 * @param port
	 * @return true or false
	 * @throws Exception
	 */
	private boolean connect(Integer port) {
		try {
			if (port == Constant.TEXT_PORT || port <= 0) {
				// connect to image port
				textFuture = connector.connect(new InetSocketAddress(
						Constant.SERVER_HOST, Constant.TEXT_PORT));
				if (textFuture.awaitUninterruptibly(Constant.CONNECT_OVERTIME)) {
					textSession = textFuture.getSession();
					textSession.setAttribute(Constant.SESSION_PORT,
							Constant.TEXT_PORT);
				} else {
					logger.warn("Text port connect falsed!");
					return false;
				}
			}
			if (port == Constant.IMAGE_PORT || port <= 0) {
				// connect to image port
				imageFuture = connector.connect(new InetSocketAddress(
						Constant.SERVER_HOST, Constant.IMAGE_PORT));
				if (imageFuture.awaitUninterruptibly(Constant.CONNECT_OVERTIME)) {
					imageSession = imageFuture.getSession();
					imageSession.setAttribute(Constant.SESSION_PORT,
							Constant.IMAGE_PORT);
				} else {
					logger.warn("Image port connect falsed!");
					return false;
				}
			} else if (port > 0 && port != Constant.TEXT_PORT
					&& port != Constant.IMAGE_PORT) {
				logger.warn("Port is illega!");
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in Client.connect: " + e.getMessage());
			return false;
		}
	}

	public void close() {
		try {
			if (connector.isActive())
				connector.dispose();
			connector = null;
			hearbeat.userStop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * init login
	 * 
	 * @param user
	 * @param accessToken
	 * @return
	 */
	public Boolean initLogin(String user, String accessToken) {
		if (!util.loginService(textSession, user, accessToken))
			return false;
		return util.loginService(imageSession, user, accessToken);
	}

	/**
	 * login again
	 * 
	 * @param session
	 * @param user
	 * @param accessToken
	 * @return
	 */
	public Boolean login(IoSession session, String user, String accessToken) {
		try {
			Integer port = (Integer) session.getAttribute(Constant.SESSION_PORT);
			Debug.println(Constant.DEBUG_INFO, "Remote session port is " + port);
			boolean flag = true;
			if (!session.isConnected()) {
				Debug.println(2, "Session is disconnected");
				connect(port);
				flag = false;
				if (Constant.TEXT_PORT.equals(port))
					flag = util.loginService(textSession, user, accessToken);
				else if(Constant.IMAGE_PORT.equals(port))
					flag = util.loginService(imageSession, user, accessToken);
			} else 
				flag = util.loginService(session, user, accessToken);
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Login failed in login again method");
			return false;
		}
	}

	/**
	 * send message
	 * 
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
	 * 
	 * @param sender
	 * @param receiver
	 * @param accessToken
	 * @param params
	 * @param filePath
	 * @return
	 */
	public boolean sendImage(String sender, String receiver,
			String accessToken, String params, byte[] file) {
		return util.sendImage(imageSession, sender, receiver, accessToken,
				params, file);
	}

	// ---------------------------------------
	// methods for overriding
	// ---------------------------------------
	/**
	 * <p>
	 * message handler
	 * </p>
	 * <p>
	 * override this method to write your service
	 * </p>
	 * 
	 * @param packet
	 * @throws Exception
	 */
	protected void messageHandler(DataPacket packet) throws Exception {

	}
	
	
	/**
	 * <p>
	 * login handler
	 * </p>
	 * <p>
	 * override this method to write your service
	 * </p>
	 * 
	 * @param packet
	 * @throws Exception
	 */
	protected void loginHandler(DataPacket packet) throws Exception {
		
	}

	/**
	 * <p>
	 * session closed
	 * </p>
	 * <p>
	 * override this method to write your service
	 * </p>
	 * 
	 * @param session
	 */
	public void closeSession(IoSession session) {

	}
}
