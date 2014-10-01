package com.java.im.core.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.java.im.core.service.OfflineMessage;
import com.java.im.util.AddressUtil;
import com.java.im.util.Debug;
import com.java.im.util.StringUtil;

public class ServerHandler extends IoHandlerAdapter {
	
	protected static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	/**
	 * <p>server message service</p>
	 * <p>error will return String object (json format)</p>
	 * <p>other will return Message/Image/String object</p>
	 */
	@Override
	public void messageReceived(IoSession session, Object message) 
			throws Exception {
		if (message instanceof DataPacket) {
			DataPacket packet = (DataPacket) message;
			// update time stamp
			packet.setTimeStamp(new Date().toString());
			String type = packet.getType();
			String sender = packet.getSender();
			String receiver = packet.getReceiver();
			String token = packet.getAccessToken();
			String address = ((InetSocketAddress) session.getRemoteAddress()).getHostName();
			// login
			if (type.equals(Constant.TYPE_LOGIN)) {
				Login login = new Login();
				packet.setSender(Constant.SERVER_NAME);
				packet.setReceiver(sender);
				if (!login.login(sender, token, address)) {
					Debug.println("login failed");
					packet.setStatus("0");
					session.write(packet);
					return;
				}
				
				// register the login status
				// get login user
				User user = GlobalResource.userMap.get(sender);
				if (user == null) {
					user = new User(sender);
				}
				// set attribute: account
				session.setAttribute(Constant.ACCOUNT, sender);
				Integer port = AddressUtil.getLocalPort(session);
				user.setIoSession(port, session);
				GlobalResource.userMap.put(sender, user);
				// return packet
				packet.setStatus("1");
				session.write(packet);
				
				Debug.println("online count: " + GlobalResource.userMap.size());
				
				// send offline message, message from cache or DB
				List<Object> cacheMsg = GlobalResource.messageQueue.get(sender);
				if (cacheMsg != null) {
					Debug.println("offline message list size: " + cacheMsg.size());
					for (int i = 0; i < cacheMsg.size(); i++) {
						session.write(cacheMsg.get(i));
					}
				}
				List<Object> dbMsg = new OfflineMessage().getOfflineMessage();
				if (dbMsg != null) {
					for (int i = 0; i < dbMsg.size(); i++) {
						session.write(dbMsg.get(i));
					}
				}
				return;
			}
			
			// check login status
			String account = (String) session.getAttribute(Constant.ACCOUNT);
			if (!GlobalResource.userMap.containsKey(account)) {
				logger.warn("no login status in this session: " + session.getRemoteAddress());
				String body = StringUtil.returnMessage(-1, "No login status");
				packet.setStatus("0");
				packet.setBody(body.getBytes(Constant.CHARSET));
				session.write(packet);
				session.close(false);
				return;
			}
			
			if (type.equals(Constant.TYPE_SEND)) {
				// get the session of receiver
				Integer toPort = AddressUtil.getLocalPort(session);
				User toUser = GlobalResource.userMap.get(receiver);
				IoSession sendSess = null;
				if (toUser != null) {
					sendSess = toUser.getIoSession(toPort);
				}
				if (sendSess != null && sendSess.getRemoteAddress() != null) {
					Debug.println("message will send to: " + receiver);
					sendSess.write(packet);// send message
				}
				else {
					if (sendSess != null) {
						// remove user?
					}
					// not online
					Debug.println("receiver " + receiver + " not online");
					List<Object> list = GlobalResource.messageQueue.get(receiver);
					if (list == null) {
						list = new ArrayList<Object>();
						Debug.println("list is null");
					}
					list.add(packet);
					GlobalResource.messageQueue.put(receiver, list); // save to messageQueue
				}
				return;
			}
			
		}
	}
	
	@Override
	public void sessionCreated(IoSession session) 
			throws Exception {
		logger.info("Session created!" + session.getRemoteAddress());
		SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
        cfg.setReceiveBufferSize(Constant.SERVER_BUFFER_SIZE);
        cfg.setReadBufferSize(Constant.SERVER_BUFFER_SIZE);
        cfg.setKeepAlive(true);
        cfg.setSoLinger(0);// 马上关闭
	}
	
	@Override
	public void sessionClosed(IoSession session)
			throws Exception {
		logger.info("Session closed: " + session.getRemoteAddress());
		removeSession(session);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		logger.warn("Session closed by exception: " + cause.getMessage());
		try {
			session.close(true);
		} catch(Exception e) {
			logger.warn("session had close!");
		}
		removeSession(session);
	}
	
	@Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
		logger.info("Session overtime! close session : " + session.getRemoteAddress());
		session.close(false);
		removeSession(session);
    }
	
	/**
	 * set session in User class as null or remove user
	 * @param session
	 */
	private void removeSession(IoSession session) {
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		User user = GlobalResource.userMap.get(account);
		if (user == null) return;
		user.setIoSession(AddressUtil.getLocalPort(session), null);
		// if not login remove user from userMap
		if (!user.ifLogin()) {
			GlobalResource.userMap.remove(account);
			logger.info(account + " is logout!");
			Debug.println("online count: " + GlobalResource.userMap.size());
		}
	}
	
}
