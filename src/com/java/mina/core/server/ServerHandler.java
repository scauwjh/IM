package com.java.mina.core.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.constant.GlobalResource;
import com.java.mina.core.model.DataPacket;
import com.java.mina.core.service.Login;
import com.java.mina.core.service.OfflineMessage;
import com.java.mina.util.AddressUtil;
import com.java.mina.util.Debug;
import com.java.mina.util.StringUtil;

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
			String contentType = packet.getContentType();
			
			// login
			if (type.equals(Constant.TYPE_LOGIN)) {
				Login login = new Login();
				if (!login.login(sender, token)) {
					packet.setStatus("0");
					session.write(packet);
					return;
				}
				
				// register the login status
				// session set account
				GlobalResource.userMap.put(packet.getSender(), token);
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
			String sessionAccount = (String) session.getAttribute(Constant.SESSION_ACCOUNT);
			String account = (String) session.getAttribute(Constant.ACCOUNT);
			if (!GlobalResource.userMap.containsKey(account)) {
				logger.warn("no login status in this session: " + session.getRemoteAddress());
				String body = StringUtil.returnMessage(-1, "No login status");
				packet.setStatus("0");
				packet.setBody(body.getBytes(Constant.CHARSET));
				session.write(packet);
				closeSession(session);
				return;
			}
			
			if (type.equals(Constant.TYPE_SEND)) {
				// get the session of receiver
				if (contentType.equals(Constant.CONTENT_TYPE_HEARTBEAT)) {
					Debug.println("Heartbeat received from: " + session.getRemoteAddress());
					return;
				}
				IoSession sendSess = GlobalResource.sessionMap.get(receiver + Constant.TEXT_PORT);
				Debug.println("sender: " + sender + "\nsession account: " + sessionAccount);
				if (sendSess != null) {
					Debug.println("message will send to: " + receiver);
					sendSess.write(packet);// send message
				}
				else {
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
	public void sessionIdle(IoSession session, IdleStatus status) 
			throws Exception {
		
	}
	
	@Override
	public void sessionCreated(IoSession session) 
			throws Exception {
		logger.info("session created: " + GlobalResource.getSessionCount(1));
	}
	
	@Override
	public void sessionClosed(IoSession session)
			throws Exception {
		logger.info("session closed: " + session.getLocalAddress() 
				+ " session count: " + GlobalResource.getSessionCount(-1));
		String sessionAccount = (String) session.getAttribute(Constant.SESSION_ACCOUNT);
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		GlobalResource.sessionMap.remove(sessionAccount);
		if (account == null) return;
		if (!GlobalResource.sessionMap.containsKey(account + Constant.TEXT_PORT) &&
				!GlobalResource.sessionMap.containsKey(account + Constant.IMAGE_PORT)) {
			logger.info("user " + account + " is logout");
			GlobalResource.userMap.remove(account);
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		logger.error("exception catch from " + session.getRemoteAddress() 
				+ ": " + cause.getLocalizedMessage());
		Debug.printStackTrace(cause);
	}
	
	private void closeSession(IoSession session) {
		session.close(false);
	}
	
}
