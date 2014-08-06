package com.java.mina.core.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.constant.GlobalResource;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.core.service.Login;
import com.java.mina.core.service.OfflineMessage;
import com.java.mina.util.AddressUtil;
import com.java.mina.util.Debug;
import com.java.mina.util.StringUtil;
import com.java.mina.util.lrucache.LRUCache;

public class ServerHandler extends IoHandlerAdapter {
	
	protected static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	private static HashMap<String, IoSession> sessionMap = GlobalResource.sessionMap;
	
	private static HashMap<String, User> userMap = GlobalResource.userMap;
	
	private static LRUCache<String, List<Object>> messageQueue = GlobalResource.messageQueue;
	
	/**
	 * <p>server message service</p>
	 * <p>error will return String object (json format)</p>
	 * <p>other will return Message/Image/String object</p>
	 */
	@Override
	public void messageReceived(IoSession session, Object message) 
			throws Exception {
		session.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
		if (message instanceof User) {
			User user = (User) message;
			String account = user.getUser();
			String password = user.getPassword();
			Login login = new Login();
			if (!login.login(account, password)) {
				session.write(StringUtil.returnMessage(-1, "login failed"));
				return;
			}
			
			// register the login status
			userMap.put(account, user);
			session.setAttribute(Constant.ACCOUNT, account);
			
			account += AddressUtil.getPort(session);
			session.setAttribute(Constant.SESSION_ACCOUNT, account);
			sessionMap.put(account, session);
			
			// send offline message, message from cache or DB
			List<Object> cacheMsg = messageQueue.get(account);
			if (cacheMsg != null) {
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
		String sessionUser = (String) session.getAttribute(Constant.SESSION_ACCOUNT);
		if (sessionUser == null) {
			logger.warn("no login status in this session: " + session.getRemoteAddress());
			session.write(StringUtil.returnMessage(-1, "no login status"));
			return;
		}
		if (message instanceof Message) {
			Message msg = (Message) message;
			String sender = msg.getSender();
			String receiver = msg.getReceiver();
			// get the session of receiver
			IoSession sendSess = sessionMap.get(receiver + Constant.TEXT_PORT);
			Debug.println("sender: " + sender + "\nsession user: " + sessionUser);
			if (sendSess != null) {
				sendSess.write(msg);// send message
				Debug.println("message will send to: " + receiver);
			}
			else {
				// not online
				Debug.println("receiver " + receiver + " not online");
				List<Object> list = messageQueue.get(receiver + Constant.TEXT_PORT);
				if (list == null) {
					list = new ArrayList<Object>();
					Debug.println("list is null");
				}
				list.add(msg);
				Debug.println("offline message list size: " + list.size());
				messageQueue.put(receiver + Constant.TEXT_PORT, list); // save to messageQueue
			}
			return;
		}
		if (message instanceof Image) {
			Image msg = (Image) message;
			String sender = msg.getSender();
			String receiver = msg.getReceiver();
			// get the session of receiver
			IoSession sendSess = sessionMap.get(receiver + Constant.IMAGE_PORT);
			Debug.println("sender: " + sender + "\nsession user: " + sessionUser);
			if (sendSess != null) {
				sendSess.write(msg);// send message
				Debug.println("message will send to: " + receiver);
			}
			else {
				// not online
				Debug.println("receiver " + receiver + " not online");
				List<Object> list = messageQueue.get(receiver + Constant.IMAGE_PORT);
				if (list == null) {
					list = new ArrayList<Object>();
					Debug.println("list is null");
				}
				list.add(msg);
				Debug.println("offline message list size: " + list.size());
				messageQueue.put(receiver + Constant.IMAGE_PORT, list); // save to messageQueue
			}
			return;
		}
		if (message instanceof String) {
			Debug.println("heartbeat is received");
			return;
		}
		if (message == null) {
			logger.warn("invalid request from: " + session.getRemoteAddress());
			session.write(StringUtil.returnMessage(-1, "invalid request"));
			return;
		}
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) 
			throws Exception {
		Long heartbeat = (Long) session.getAttribute(Constant.HEARTBEAT);
		if (heartbeat != null && System.currentTimeMillis() 
				- heartbeat > Constant.SESSION_OVERTIME) {
			session.close(false);
			logger.info("session from " + session.getRemoteAddress() + " is overtime");
		}
	}
	
	@Override
	public void sessionCreated(IoSession session) 
			throws Exception {
		logger.info("session created: " + ++Server.SESSION_COUNT);
		session.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
	}
	
	@Override
	public void sessionClosed(IoSession session)
			throws Exception {
		String sessionAccount = (String) session.getAttribute(Constant.SESSION_ACCOUNT);
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		logger.info("session closed: " + --Server.SESSION_COUNT);
		sessionMap.remove(sessionAccount);
		if (!sessionMap.containsKey(account + Constant.TEXT_PORT) &&
				!sessionMap.containsKey(account + Constant.IMAGE_PORT)) {
			logger.info("user " + account + " is logout");
			userMap.remove(account);
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		logger.error("exception catch from " + session.getRemoteAddress());
		logger.error(cause.getMessage());
	}
	
}
