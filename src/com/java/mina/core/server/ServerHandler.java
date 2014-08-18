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
import com.java.mina.core.model.Heartbeat;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
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
		session.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
		String randStr = StringUtil.randString(32);
		// if heartbeat
		if (message instanceof Heartbeat) {
			Boolean flag = true;
			Debug.println("heartbeat is received");
			Heartbeat hb = (Heartbeat) message;
			String acc = hb.getAccount();
			IoSession sess = GlobalResource.sessionMap.get(acc + Constant.TEXT_PORT);
			if (sess == null) {
				logger.warn("text port session is null for heartbeat");
				flag = false;
				return;
			}
			sess.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
			sess = GlobalResource.sessionMap.get(acc + Constant.IMAGE_PORT);
			if (sess == null) {
				logger.warn("text port session is null for heartbeat");
				flag = false;
				return;
			}
			sess.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
			if (flag)
				Debug.println(acc + " is beat success!");
			else
				hb.setAccount(randStr);
			hb.setTimeStamp(new Date().toString());
			session.write(hb);
			return;
		}
		if (message instanceof User) {
			User user = (User) message;
			String account = user.getUser();
			String password = user.getPassword();
			Login login = new Login();
			if (!login.login(account, password)) {
				user.setPassword(randStr);
				user.setStatus(0);
				session.write(user);
				return;
			}
			
			// register the login status
			GlobalResource.userMap.put(account, user);
			session.setAttribute(Constant.ACCOUNT, account);
			Debug.println("online count: " + GlobalResource.userMap.size());
			String sessionAccount = account + AddressUtil.getPort(session);
			session.setAttribute(Constant.SESSION_ACCOUNT, sessionAccount);
			GlobalResource.sessionMap.put(sessionAccount, session);
			user.setPassword(randStr);
			user.setStatus(1);
			session.write(user);
			
			// send offline message, message from cache or DB
			List<Object> cacheMsg = GlobalResource.messageQueue.get(account);
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
		String sessionUser = (String) session.getAttribute(Constant.SESSION_ACCOUNT);
		String account = (String) session.getAttribute(Constant.ACCOUNT);
		if (!GlobalResource.userMap.containsKey(account)) {
			logger.warn("no login status in this session: " + session.getRemoteAddress());
			session.write(StringUtil.returnMessage(-1, "no login status"));
			closeSession(session);
			return;
		}
		if (message instanceof Message) {
			Message msg = (Message) message;
			String sender = msg.getSender();
			String receiver = msg.getReceiver();
			// update timeStamp
			msg.setTimeStamp(new Date().toString());
			// get the session of receiver
			IoSession sendSess = GlobalResource.sessionMap.get(receiver + Constant.TEXT_PORT);
			Debug.println("sender: " + sender + "\nsession user: " + sessionUser);
			if (sendSess != null) {
				sendSess.write(msg);// send message
				Debug.println("message will send to: " + receiver);
			}
			else {
				// not online
				Debug.println("receiver " + receiver + " not online");
				List<Object> list = GlobalResource.messageQueue.get(receiver);
				if (list == null) {
					list = new ArrayList<Object>();
					Debug.println("list is null");
				}
				list.add(msg);
				GlobalResource.messageQueue.put(receiver, list); // save to messageQueue
			}
			return;
		}
		if (message instanceof Image) {
			Image msg = (Image) message;
			String sender = msg.getSender();
			String receiver = msg.getReceiver();
			// update timeStamp
			msg.setTimeStamp(new Date().toString());
			// get the session of receiver
			IoSession sendSess = GlobalResource.sessionMap.get(receiver + Constant.IMAGE_PORT);
			Debug.println("sender: " + sender + "\nsession user: " + sessionUser);
			if (sendSess != null) {
				sendSess.write(msg);// send message
				Debug.println("message will send to: " + receiver);
			}
			else {
				// not online
				Debug.println("receiver " + receiver + " not online");
				List<Object> list = GlobalResource.messageQueue.get(receiver);
				if (list == null) {
					list = new ArrayList<Object>();
					Debug.println("list is null");
				}
				list.add(msg);
				GlobalResource.messageQueue.put(receiver, list); // save to messageQueue
			}
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
		Long systemTime = System.currentTimeMillis();
		if (heartbeat != null && (systemTime - heartbeat)
				> Constant.SESSION_OVERTIME) {
			closeSession(session);
			logger.info("session from " + session.getRemoteAddress() + " is overtime");
			Debug.println("beat time: " + heartbeat);
			Debug.println("system time: " + systemTime);
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
		GlobalResource.sessionMap.remove(sessionAccount);
		session.removeAttribute(Constant.SESSION_ACCOUNT);
		session.removeAttribute(Constant.ACCOUNT);
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
		logger.error("exception catch from " + session.getRemoteAddress());
		logger.error(cause.getMessage());
	}
	
	protected void closeSession(IoSession session) {
		session.close(false);
	}
	
}
