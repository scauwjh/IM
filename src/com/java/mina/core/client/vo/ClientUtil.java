package com.java.mina.core.client.vo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.core.client.Client;
import com.java.mina.core.model.DataPacket;
import com.java.mina.util.AddressUtil;
import com.java.mina.util.Debug;
import com.java.mina.util.ImageUtil;

public class ClientUtil {
	
	public final static Logger logger = LoggerFactory.getLogger(ClientUtil.class);
	
	/**
	 * 发送登录信息
	 * @param session
	 * @param account
	 * @param password
	 * @return
	 */
	public Boolean login(IoSession session, String account, String password) {
		try {
			Debug.println("#####login begin#####");
			if (session.isClosing()) {
				int port = AddressUtil.getRemotePort(session);
				if (port < 0) {
					return initAgain(account, password);
				}
				IoSession sess = connectAgain(port);
				if (sess == null)
					return false;
				session = sess;
				if (port == Constant.TEXT_PORT) {
					Client.textSession = session;
				} else if (port == Constant.IMAGE_PORT) {
					Client.imageSession = session;
				}
			}
			DataPacket packet = new DataPacket();
			packet.setType(Constant.TYPE_LOGIN);
			packet.setSender(account);
			packet.setAccessToken(password);
			packet.setContentType(Constant.CONTENT_TYPE_LOGIN);
			packet.setTimeStamp(new Date().toString());
			packet.setStatus("0");
			// send login message
			WriteFuture write = session.write(packet);
			if (!write.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				Debug.println("Failed to write: " + session.getRemoteAddress());
				return false;
			}
			if (!write.isWritten())
				return false;
			// read return message
			ReadFuture read = session.read();
			if (read.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				packet = (DataPacket) read.getMessage();
				if (packet.getStatus().equals("1")) {
					Debug.println("Login succeed");
					return true;
				} else {
					Debug.println("Login failed");
					return false;
				}
			} else {
				Debug.println("Failed to read: " + session.getRemoteAddress());
				return false;
			}
		} catch (Exception e) {
			logger.error("Error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
	
	private Boolean initAgain(String account, String password) {
		IoSession sess = connectAgain(Constant.TEXT_PORT);
		if (sess == null) {
			Debug.println("Failed to init text session");
			return false;
		}
		Client.textSession = sess;
		if (!login(Client.textSession, account, password)) {
			Debug.println("Failed to login again at text session");
			return false;
		}
		sess = connectAgain(Constant.IMAGE_PORT);
		if (sess == null) {
			Debug.println("Failed to init image session");
			return false;
		}
		Client.imageSession = sess;
		if (!login(Client.imageSession, account, password)) {
			Debug.println("Failed to login again at image session");
			return false;
		}
		Debug.println("Succeed to init again");
		return true;
	}
	
	private IoSession connectAgain(Integer port) {
		ConnectFuture conn = null;
		Debug.println("Connect to port: " + port + " again!!");
		conn = Client.connector.connect(new InetSocketAddress(
				Constant.SERVER_HOST, port));
		if (conn.awaitUninterruptibly(Constant.CONNECT_OVERTIME)) {
			return conn.getSession();
		} else {
			Debug.println("Failed to connect port: " + port);
			return null;
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
	 * @return
	 */
	private Boolean sendData(IoSession session, String sender, String receiver, 
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
			msg.setBody(body);
			WriteFuture write = session.write(msg);
			if (!write.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				Debug.println("Failed to write!!!");
				return false;
			}
			if (write.isWritten())
				return true;
			return false;
		} catch (Exception e) {
			logger.error("Send data error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
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
	 * @return
	 */
	public Boolean sendMessage(IoSession session, String sender, String receiver,
			String accessToken, String params, String message) {
		try {
			byte[] body = message.getBytes(Constant.CHARSET);
			return sendData(session, sender, receiver, accessToken, "1", 
					Constant.CONTENT_TYPE_MESSAGE, params, body);
		} catch (UnsupportedEncodingException e) {
			logger.error("Send message error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
	
	/**
	 * 发送心跳包
	 * @param session
	 * @param account
	 * @param accountToken
	 * @param params
	 * @return
	 */
	public Boolean sendHeartbeat(IoSession session, String account, 
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
	 * @param filePath
	 * @return
	 */
	public Boolean sendImage(IoSession session, String sender, 
			String receiver, String accessToken, String params, String filePath) {
		InputStream in;
		try {
			in = new FileInputStream(filePath);
			byte[] dst = ImageUtil.imageCompress(in, 0.9, 1.0);
			return sendData(session, sender, receiver, accessToken, "1", 
					Constant.CONTENT_TYPE_IMAGE, params, dst);
		} catch (Exception e) {
			logger.error("Send image error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
}
