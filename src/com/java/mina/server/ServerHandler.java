package com.java.mina.server;

import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.util.Debug;

public class ServerHandler extends IoHandlerAdapter {
	
	protected static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	protected static HashMap<String, String> messageQueue = new HashMap<String, String>();
	
	private static HashMap<String, IoSession> sessions = new HashMap<String, IoSession>();
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) 
			throws Exception {
		cause.printStackTrace();
		logger.error("exception catch from " + session.getRemoteAddress());
		logger.error(cause.getMessage());
		cause.printStackTrace();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) 
			throws Exception {
		session.setAttribute(Constant.HEARTBEAT_KEY, System.currentTimeMillis());
		String msg = (String) message;
		String[] str = msg.split("@");
		if (str.length < 2) {
			Debug.println("heartbeat is received");
			session.setAttribute(Constant.HEARTBEAT_KEY, System.currentTimeMillis());
			session.setAttribute(Constant.ACCOUNT, str[0]);
			sessions.put(str[0], session);
		} else {
			String fromUser = (String) session.getAttribute(Constant.ACCOUNT);
			String toUser = str[0];
			Debug.println("a message received from: " + fromUser);
			IoSession sendSess = sessions.get(toUser);
			Debug.println("message will send to: " + toUser);
			if (sendSess != null) {
				String sendMsg = fromUser + "@" + str[1]; // send message
				sendSess.write(sendMsg);
			} else {
				Debug.println("session not exit");
				messageQueue.put(toUser, str[1]); // save to messageQueue
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
	
}
