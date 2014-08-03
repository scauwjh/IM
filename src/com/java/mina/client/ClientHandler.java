package com.java.mina.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.util.Debug;

public class ClientHandler extends IoHandlerAdapter {
	
	public final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		logger.info("message received from: " + session.getRemoteAddress());
		Debug.println("message received:");
		Debug.println(message);
	}
	
	@Override
	public void messageSent(IoSession session, Object message)
			throws Exception {
		logger.info("message sent to: " + session.getRemoteAddress());
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
		throws Exception {
		logger.error("exception throw: " + cause.getMessage());
		cause.printStackTrace();
	}
}
