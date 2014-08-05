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
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.ReturnMessage;
import com.java.mina.core.model.User;
import com.java.mina.core.service.OfflineMessage;
import com.java.mina.util.Debug;
import com.java.mina.util.StringUtil;
import com.java.mina.util.lrucache.LRUCache;

public class ServerHandler extends IoHandlerAdapter {
	
	protected static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	private static HashMap<String, IoSession> sessions = new HashMap<String, IoSession>();
	
	private static LRUCache<String, List<Object>> messageQueue = OfflineMessage.messageQueue;
	
	@Override
	public void messageReceived(IoSession session, Object message) 
			throws Exception {
		session.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
		if (message instanceof User) {
			User user = (User) message;
			String account = user.getUser();
//			String password = user.getPassword();
			// ..................................................
			// add login service, need to get the offline message
			// ..................................................
			session.setAttribute(Constant.ACCOUNT, account);
			sessions.put(account, session);
			
			// if needed to process it by other thread ?
			List<Object> offlineMsg = messageQueue.get(account);
			if (offlineMsg != null) {
				Debug.println("login offline message list size: " + offlineMsg.size());
				for (int i = 0; i < offlineMsg.size(); i++) {
					session.write(offlineMsg.get(i));
					Debug.println("write " + i);
				}
			}
			return;
		}
		// check login status
		String sessionUser = (String) session.getAttribute(Constant.ACCOUNT);
		if (sessionUser == null) {
			logger.warn("no login status in this session: " + session.getRemoteAddress());
			Debug.println("no login status");
			session.write(StringUtil.returnMessage(-1, "no login status"));
			return;
		}
		if (message instanceof Message) {
			Message msg = (Message) message;
			String sender = msg.getSender();
			String receiver = msg.getReceiver();
			// get the session of receiver
			IoSession sendSess = sessions.get(receiver);
			Debug.println("sender: " + sender + "\nsession user: " + sessionUser);
			if (sendSess != null) {
				sendSess.write(msg);// send message
				Debug.println("message will send to: " + receiver);
			}
			else {
				// not online
				Debug.println("receiver " + receiver + " not online");
				List<Object> list = messageQueue.get(receiver);
				if (list == null) {
					list = new ArrayList<Object>();
					Debug.println("list is null");
				}
				list.add(msg);
				Debug.println("offline message list size: " + list.size());
				messageQueue.put(receiver, list); // save to messageQueue
			}
			return;
		}
		if (message instanceof Image) {
			Image msg = (Image) message;
			String sender = msg.getSender();
			String receiver = msg.getReceiver();
			// get the session of receiver
			IoSession sendSess = sessions.get(receiver);
			Debug.println("sender: " + sender + "\nsession user: " + sessionUser);
			if (sendSess != null) {
				sendSess.write(msg);// send message
				Debug.println("message will send to: " + receiver);
			}
			else {
				// not online
				Debug.println("receiver " + receiver + " not online");
				List<Object> list = messageQueue.get(receiver);
				if (list == null) {
					list = new ArrayList<Object>();
					Debug.println("list is null");
				}
				list.add(msg);
				Debug.println("offline message list size: " + list.size());
				messageQueue.put(receiver, list); // save to messageQueue
			}
			return;
		}
		if (message instanceof String) {
			Debug.println("heartbeat is received");
			return;
		}
		if (message == null) {
			logger.warn("invalid request from: " + session.getRemoteAddress());
			Debug.println("invalid request");
			ReturnMessage ret = new ReturnMessage();
			ret.setCode(-1);
			ret.setMsg("no login status");
			session.write(ret);
			return;
		}
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) 
			throws Exception {
		Object heartbeat = session.getAttribute(Constant.HEARTBEAT);
		if (heartbeat != null && System.currentTimeMillis() 
				- Long.valueOf(heartbeat.toString()) > Constant.SESSION_OVERTIME) {
			session.close(false);
			Debug.println("a session is overtime");
		}
	}
	
	@Override
	public void sessionCreated(IoSession session) 
			throws Exception {
		Debug.println("session created: " + ++MINAServer.SESSION_COUNT);
		session.setAttribute(Constant.HEARTBEAT, System.currentTimeMillis());
	}
	
	@Override
	public void sessionClosed(IoSession session)
			throws Exception {
		Debug.println("session closed: " + --MINAServer.SESSION_COUNT);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		logger.error("exception catch from " + session.getRemoteAddress());
		logger.error(cause.getMessage());
//		cause.printStackTrace();
	}
	
}
