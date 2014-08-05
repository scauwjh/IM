package com.java.mina.core.client;

import java.io.FileOutputStream;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
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
		} else if (message instanceof Image) {
			Image msg = (Image) message;
			System.out.println("message received form: " + msg.getSender());
			System.out.println("timeStamp: " + msg.getTimeStamp());
			String file = "C:\\Users\\asus\\Desktop\\rec.jpg";
			FileOutputStream out = new FileOutputStream(file);
			out.write(msg.getImage());
			out.close();
		} else if (message instanceof String) {
			System.out.println(message);
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
//		cause.printStackTrace();
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.warn("lost connection from server");
		Debug.println("lost connection from server");
	}
}
