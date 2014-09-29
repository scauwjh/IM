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
				packet.setSender("server\n");
				packet.setReceiver(sender);
				if (!login.login(sender, token, address)) {
					packet.setStatus("0");
					session.write(packet);
					return;
				}
				
				// register the login status
				// session set account
				GlobalResource.userMap.put(sender, session);
				session.setAttribute(Constant.ACCOUNT, sender);
				// session set session_account
				String sessionAccount = sender + AddressUtil.getLocalPort(session);
				session.setAttribute(Constant.SESSION_ACCOUNT, sessionAccount);
				// add session to map
				GlobalResource.sessionMap.put(sessionAccount, session);
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
				String sendTo = receiver + AddressUtil.getLocalPort(session);
				IoSession sendSess = GlobalResource.sessionMap.get(sendTo);
				if (sendSess != null && sendSess.getRemoteAddress() != null) {
					Debug.println("message will send to: " + receiver);
					sendSess.write(packet);// send message
				}
				else {
					if (sendSess != null)
						GlobalResource.sessionMap.remove(sendTo);
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
		logger.info("Session created: " + GlobalResource.getSessionCount(1));
		session.setAttribute(Constant.IS_SESSION_CLOSE, false);
		SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
        cfg.setReceiveBufferSize(Constant.SERVER_BUFFER_SIZE);
        cfg.setReadBufferSize(Constant.SERVER_BUFFER_SIZE);
        cfg.setKeepAlive(true);
        cfg.setSoLinger(0);// 马上关闭
	}
	
	@Override
	public void sessionClosed(IoSession session)
			throws Exception {
		Boolean isClose = (Boolean) session.getAttribute(Constant.IS_SESSION_CLOSE);
		if (!isClose) {
			logger.info("Session closed: " + session.getRemoteAddress());
			userLogout(session);
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		Boolean isClose = (Boolean) session.getAttribute(Constant.IS_SESSION_CLOSE);
		if (!isClose) {
			logger.warn("Session closed by exception: " + cause.getMessage());
			userLogout(session);
		} else {
			logger.warn("Exception caught: " + cause.getMessage());
		}
	}
	
	@Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
		logger.info("Session overtime! close session : " + session.getRemoteAddress());
		session.close(false);
    }
	
	
	
	
	private void userLogout(IoSession session) {
		session.setAttribute(Constant.IS_SESSION_CLOSE, true);
		logger.info("Session count is: " + GlobalResource.getSessionCount(-1));
		String sessionAccount = (String) session.getAttribute(Constant.SESSION_ACCOUNT);
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		GlobalResource.sessionMap.remove(sessionAccount);
		if (account == null) return;
		if (!GlobalResource.sessionMap.containsKey(account + Constant.TEXT_PORT) &&
				!GlobalResource.sessionMap.containsKey(account + Constant.IMAGE_PORT)) {
			logger.info("User " + account + " is logout");
			GlobalResource.userMap.remove(account);
		}
	}
	
}
