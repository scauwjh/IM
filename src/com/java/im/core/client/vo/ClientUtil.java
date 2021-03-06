package com.java.im.core.client.vo;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.model.DataPacket;
import com.java.im.util.Debug;
import com.java.im.util.StringUtil;

public class ClientUtil {
	
	public final static Logger logger = LoggerFactory.getLogger(ClientUtil.class);
	
	
	
	public Boolean loginService(IoSession session, String account, String accessToken) {		
		try {
			DataPacket packet = new DataPacket();
			packet.setType(Constant.TYPE_LOGIN);
			packet.setSender(account);
			packet.setReceiver(Constant.SERVER_NAME);
			packet.setAccessToken(accessToken);
			packet.setContentType(Constant.CONTENT_TYPE_LOGIN);
			packet.setTimeStamp(new Date().toString());
			Long ctime = System.currentTimeMillis();
			packet.setIdentification(ctime.toString() + StringUtil.randString(6));
			packet.setStatus("0");
			// send login message
			WriteFuture write = session.write(packet);
			if (write.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				if (!write.isWritten()) {
					Debug.println(Constant.DEBUG_INFO, "Failed to write to: " + session.getRemoteAddress());
					return false;
				}
			} else {
				Debug.println(Constant.DEBUG_INFO, "Write message over time in loginServeice");
				return false;
			}
			// read return message
			ReadFuture read = session.read();
			if (read.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				packet = (DataPacket) read.getMessage();
				if (packet.getStatus().equals("1")) {
					Debug.println(Constant.DEBUG_INFO, "Login succeed");
					return true;
				} else {
					Debug.println(Constant.DEBUG_INFO, "Login failed");
					return false;
				}
			} else {
				Debug.println(Constant.DEBUG_INFO, "Read message over time in loginServeice");
				return false;
			}
		} catch (Exception e) {
			Debug.printStackTrace(Constant.DEBUG_INFO, e);
			logger.warn("Exception in loginService method");
			return false;
		}
	}
	
	/**
	 * send data
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param accessToken
	 * @param status
	 * @param contentType
	 * @param parameters
	 * @param body
	 * @return DataPacket or null
	 */
	private DataPacket sendData(IoSession session, String sender, String receiver, 
			String accessToken, String status, String contentType, String parameters, byte[] body) {
		try {
			DataPacket msg = new DataPacket();
			msg.setType(Constant.TYPE_SEND);
			msg.setSender(sender);
			msg.setReceiver(receiver);
			msg.setAccessToken(accessToken);
			msg.setStatus(status);
			msg.setContentType(contentType);
			msg.setTimeStamp(new Date().toString());
			msg.setParameters(parameters);
			Long ctime = System.currentTimeMillis();
			msg.setIdentification(ctime.toString() + StringUtil.randString(6));
			msg.setBody(body);
			session.write(msg);
			return msg;
		} catch (Exception e) {
			logger.error("Send data error: " + e.getLocalizedMessage());
			Debug.printStackTrace(Constant.DEBUG_INFO, e);
			return null;
		}
	}
	
	
	/**
	 * 发送文字信息
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param accessToken
	 * @param params
	 * @param message
	 * @return DataPacket or null
	 */
	public DataPacket sendMessage(IoSession session, String sender, String receiver,
			String accessToken, String params, String message) {
		try {
			byte[] body = message.getBytes(Constant.CHARSET);
			return sendData(session, sender, receiver, accessToken, "1", 
					Constant.CONTENT_TYPE_MESSAGE, params, body);
		} catch (UnsupportedEncodingException e) {
			logger.error("Send message error: " + e.getLocalizedMessage());
			Debug.printStackTrace(Constant.DEBUG_INFO, e);
			return null;
		}
	}
	
	/**
	 * 发送心跳包
	 * @param session
	 * @param account
	 * @param accountToken
	 * @param params
	 * @return DataPacket or null
	 */
	public DataPacket sendHeartbeat(IoSession session, String account, 
			String accessToken, String params) {
		return sendData(session, account, account, accessToken, "1", 
				Constant.CONTENT_TYPE_HEARTBEAT, params, null);
	}
	
	/**
	 * 发送图片
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param accessToken
	 * @param params
	 * @param file
	 * @return DataPacket or null
	 */
	public DataPacket sendImage(IoSession session, String sender, 
			String receiver, String accessToken, String params, byte[] file) {
		try {
			return sendData(session, sender, receiver, accessToken, "1", 
					Constant.CONTENT_TYPE_IMAGE, params, file);
		} catch (Exception e) {
			logger.error("Send image error: " + e.getLocalizedMessage());
			Debug.printStackTrace(Constant.DEBUG_INFO, e);
			return null;
		}
	}
}
