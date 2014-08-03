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
import com.java.mina.core.model.ReceivedBody;
import com.java.mina.core.service.OfflineMessage;
import com.java.mina.util.Debug;
import com.java.mina.util.JsonUtil;
import com.java.mina.util.lrucache.LRUCache;

public class ServerHandler extends IoHandlerAdapter {
	
	protected static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	private static HashMap<String, IoSession> sessions = new HashMap<String, IoSession>();
	
	private static LRUCache<String, List<String>> messageQueue = OfflineMessage.messageQueue;
	
	// if id needed to process by other thread?
	@Override
	public void messageReceived(IoSession session, Object message) 
			throws Exception {
		session.setAttribute(Constant.HEARTBEAT_KEY, System.currentTimeMillis());
		String msg = (String) message;
		Debug.println("message received:\n" + msg);
		if (msg.equals("")) {
			Debug.println("heartbeat is received");
			return;
		}
		ReceivedBody body = (ReceivedBody) JsonUtil.toObject(msg, ReceivedBody.class);
		if (body == null) {
			Debug.println("invalid request");
			return;
		}
		String method = body.getMethod(); // method
		String param1 = body.getParam1(); // user
		String param2 = body.getParam2(); // password or message
		// login
		// login@user@password
		if (method.equals("login")) {
			// ..................................................
			// add login service, need to get the offline message
			// ..................................................
			session.setAttribute(Constant.ACCOUNT, param1);
			sessions.put(param1, session);
			return;
		}
		
		// check login status
		String fromUser = (String) session.getAttribute(Constant.ACCOUNT);
		if (fromUser == null) {
			String tmp = "no login status in this session: " + session.getRemoteAddress();
			logger.warn(tmp);
			Debug.println(tmp);
			session.write("{\"ret\":\"-1\", \"msg\":\"no login status\"}");
			return;
		}
		// send message
		// send@toUser@message
		if (method.equals("send")) {
			IoSession sendSess = sessions.get(param1);
			Debug.println("a message received from: " + fromUser);
			if (sendSess != null) {
				sendSess.write("{\"from\":\"" + fromUser + "\", \"msg\":\"" + param2 + "\"}");// send message
				Debug.println("message will send to: " + param1);
			}
			else {
				// not online
				Debug.println("session not exit");
				List<String> list = messageQueue.get(param1);
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(param2);
				messageQueue.put(param1, list); // save to messageQueue
			}
		}
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) 
			throws Exception {
		Object heartbeat = session.getAttribute(Constant.HEARTBEAT_KEY);
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
		session.setAttribute(Constant.HEARTBEAT_KEY, System.currentTimeMillis());
	}
	
	@Override
	public void sessionClosed(IoSession session)
			throws Exception {
		Debug.println("session closed: " + --MINAServer.SESSION_COUNT);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		cause.printStackTrace();
		logger.error("exception catch from " + session.getRemoteAddress());
		logger.error(cause.getMessage());
		cause.printStackTrace();
	}
	
}
