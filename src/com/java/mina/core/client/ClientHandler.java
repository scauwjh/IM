package com.java.mina.core.client;

import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.core.model.Message;
import com.java.mina.core.model.ReturnMessage;
import com.java.mina.util.Debug;

public class ClientHandler extends IoHandlerAdapter {
	
	public final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		logger.info("message received from: " + session.getRemoteAddress());
		if (message instanceof Message) {
			Message msg = (Message) message;
			System.out.println("message received form: " + msg.getSender());
			System.out.println("timeStamp: " + msg.getTimeStamp());
			System.out.println("message:\n" + msg.getMessage());
		} else if (message instanceof List) {
			@SuppressWarnings("unchecked")
			List<Message> msgList = (List<Message>) message;
			Debug.println("message list received list size: " + msgList.size());
			for (int i = 0; i < msgList.size(); i++) {
				Message msg = msgList.get(i);
				System.out.println("message received form: " + msg.getSender());
				System.out.println("timeStamp: " + msg.getTimeStamp());
				System.out.println("message:\n" + msg.getMessage());
			}
		} else if (message instanceof ReturnMessage) {
			ReturnMessage ret = (ReturnMessage) message;
			System.out.println("ret code: " + ret.getCode());
			System.out.println("ret message: " + ret.getMsg());
		}
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
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.warn("lost connection from server");
		Debug.println("lost connection from server");
	}
}
