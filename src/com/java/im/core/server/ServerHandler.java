package com.java.im.core.server;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Element;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.constant.GlobalResource;
import com.java.im.core.model.DataPacket;
import com.java.im.core.model.User;
import com.java.im.core.service.Login;
import com.java.im.util.AddressUtil;
import com.java.im.util.Debug;
import com.java.im.util.StringUtil;

public class ServerHandler extends IoHandlerAdapter {

	protected static final Logger logger = LoggerFactory
			.getLogger(ServerHandler.class);

	/**
	 * <p>
	 * server message service
	 * </p>
	 * <p>
	 * error will return String object (json format)
	 * </p>
	 * <p>
	 * other will return Message/Image/String object
	 * </p>
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message instanceof DataPacket) {
			DataPacket packet = (DataPacket) message;
			String sender = packet.getSender();
			String type = packet.getType();
			// update time stamp
			packet.setTimeStamp(new Date().toString());
			// login
			if (type.equals(Constant.TYPE_LOGIN)) {
				if (!login(session, packet))
					return;
				sendOfflineMessage(session, sender);
			}
			// check login status
			if (!checkLoginStatus(session, packet))
				return;
			if (type.equals(Constant.TYPE_SEND)) {
				sendMessage(session, packet);
			}
		}
	}

	/**
	 * login service
	 * 
	 * @param session
	 * @param packet
	 * @return true or false
	 */
	private boolean login(IoSession session, DataPacket packet) {
		Login login = new Login();
		String token = packet.getAccessToken();
		String sender = packet.getSender();
		String address = ((InetSocketAddress) session.getRemoteAddress())
				.getHostName();
		Integer port = AddressUtil.getLocalPort(session);
		packet.setSender(Constant.SERVER_NAME + port);
		packet.setReceiver(sender);

		if (!login.login(sender, token, address)) {
			// login failed
			Debug.println(Constant.DEBUG_INFO, "Login failed");
			packet.setStatus("0");
			packet.setType(Constant.TYPE_LOGIN);
			session.write(packet);
			return false;
		}

		// register the login status
		// get login user
		User user = GlobalResource.userMap.get(sender);
		if (user == null) {
			user = new User(sender);
		}
		// set attribute: account
		session.setAttribute(Constant.ACCOUNT, sender);
		user.setIoSession(port, session);
		GlobalResource.userMap.put(sender, user);
		// return packet
		packet.setStatus("1");
		packet.setType(Constant.TYPE_LOGIN);
		session.write(packet);
		Debug.println(Constant.DEBUG_INFO, "online count: "
				+ GlobalResource.userMap.size());
		return true;
	}

	/**
	 * check login status
	 * 
	 * @param session
	 * @param packet
	 * @return true or false
	 */
	private boolean checkLoginStatus(IoSession session, DataPacket packet) {
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		if (!GlobalResource.userMap.containsKey(account)) {
			logger.warn("no login status in this session: "
					+ session.getRemoteAddress());

			packet.setStatus("0");
			String body = StringUtil.returnMessage(-1, "No login status");
			try {
				packet.setBody(body.getBytes(Constant.CHARSET));
			} catch (UnsupportedEncodingException e) {
				logger.warn("String to bytes error in checkLoginStatus");
			}
			packet.setType(Constant.TYPE_RETURN);
			session.write(packet);
			session.close(false);
			return false;
		}
		return true;
	}

	/**
	 * send offline message
	 * 
	 * @param session
	 * @param user
	 */
	private void sendOfflineMessage(IoSession session, String user) {
		// send offline message
		synchronized (this) {
			Debug.println(Constant.DEBUG_DEBUG, "get offile message: " + user);
			Element msgElement = GlobalResource.messageCache.get(user);
			if (msgElement != null) {
				@SuppressWarnings("unchecked")
				List<DataPacket> msgList = (List<DataPacket>) msgElement.getObjectValue();
				Debug.println(Constant.DEBUG_INFO, "Offline message list size: "
						+ msgList.size());
				for (int i = 0; i < msgList.size(); i++) {
					Debug.println(Constant.DEBUG_INFO, "offline message sender: "
							+ msgList.get(i).getSender() + " receover: " + msgList.get(i).getReceiver());
					session.write(msgList.get(i));
				}
				GlobalResource.messageCache.remove(user);
			}
		}
	}

	/**
	 * send message to user
	 * 
	 * @param session
	 * @param packet
	 */
	@SuppressWarnings("unchecked")
	private void sendMessage(IoSession session, DataPacket packet) {
		// get the session of receiver
		Integer toPort = AddressUtil.getLocalPort(session);
		String receiver = packet.getReceiver();
		String sender = packet.getSender();
		User toUser = GlobalResource.userMap.get(receiver);
		IoSession sendSess = null;

		if (toUser != null) {
			sendSess = toUser.getIoSession(toPort);
		}
		if (sendSess != null && sendSess.getRemoteAddress() != null) {
			Debug.println(Constant.DEBUG_DEBUG, "Message will send to: "
					+ receiver);
			// send message
			sendSess.write(packet);
			// return status to sender
			session.write(createReturnPacket(sender, Constant.STATUS_SUCCESS));
			return;
		} else {
			// return status to sender
			session.write(createReturnPacket(sender, Constant.STATUS_OFFLINE));
			
			if (sendSess != null) {
				// remove user?
				GlobalResource.userMap.remove(receiver);
				if (!sendSess.isClosing())
					sendSess.close(true);
			}
			// not online
			Debug.println(Constant.DEBUG_DEBUG, "Receiver " + receiver
					+ " not online");
			synchronized (this) {
				Element msgElement = GlobalResource.messageCache.get(receiver);
				List<DataPacket> list = null;
				if (msgElement == null) {
					Debug.println(Constant.DEBUG_DEBUG, "element is null");
					list = new ArrayList<DataPacket>();
					list.add(packet);
					msgElement = new Element(receiver, list);
					GlobalResource.messageCache.put(msgElement);
				} else {
					Debug.println(Constant.DEBUG_DEBUG, "element is not null");
					list = (List<DataPacket>) msgElement.getObjectValue();
					list.add(packet);
				}
			}
		}
	}
	
	private DataPacket createReturnPacket(String receiver, String status) {
		DataPacket dp = new DataPacket();
		dp.setType(Constant.TYPE_RETURN);
		dp.setReceiver(receiver);
		dp.setStatus(status);
		dp.setSender(Constant.SERVER_NAME);
		dp.setBody(null);
		return dp;
	}

	/**
	 * set session in User class as null or remove user
	 * 
	 * @param session
	 */
	private void removeSession(IoSession session) {
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		User user = GlobalResource.userMap.get(account);
		if (user == null)
			return;
		user.setIoSession(AddressUtil.getLocalPort(session), null);
		// if not login remove user from userMap
		if (!user.ifLogin()) {
			GlobalResource.userMap.remove(account);
			logger.info(account + " is logout!");
			Debug.println(Constant.DEBUG_INFO, "Online count: "
					+ GlobalResource.userMap.size());
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("Session created!" + session.getRemoteAddress());
		SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
		cfg.setReceiveBufferSize(Constant.SERVER_BUFFER_SIZE);
		cfg.setReadBufferSize(Constant.SERVER_BUFFER_SIZE);
		cfg.setKeepAlive(true);
		cfg.setSoLinger(0);// 马上关闭
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("Session closed: " + session.getRemoteAddress());
		removeSession(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.warn("Session closed by exception: " + cause.getMessage());
		try {
			session.close(true);
		} catch (Exception e) {
			logger.warn("session had close!");
		}
		removeSession(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		logger.info("Session overtime! close session : "
				+ session.getRemoteAddress());
		session.close(false);
		removeSession(session);
	}

}
