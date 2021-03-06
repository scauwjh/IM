package com.java.im.core.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.util.Debug;

public class ClientHandler extends IoHandlerAdapter {
	
	public final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		logger.info("message received from: " + session.getRemoteAddress());
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
		Debug.printStackTrace(Constant.DEBUG_INFO, cause);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.warn("lost connection from server");
	}
}
