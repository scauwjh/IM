package com.java.mina.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.constant.GlobalResource;
import com.java.mina.core.client.Client;
import com.java.mina.core.model.Heartbeat;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.util.AddressUtil;
import com.java.mina.util.Debug;
import com.java.mina.util.ImageUtil;

public class APIInstance implements API {
	
	public final static Logger logger = LoggerFactory.getLogger(APIInstance.class);
	
	
	/**
	 * 服务器总在线人数
	 * @return
	 */
	public Integer onlineCount() {
		return GlobalResource.userMap.size();
	}
	
	/**
	 * 检查用户是否在线
	 * @param account
	 * @return
	 */
	public Boolean ifOnline(String account) {
		return GlobalResource.userMap.containsKey(account);
	}
	
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
				IoSession sess = connectAgain(port);
				if (sess == null)
					return false;
				session = sess;
				if (port == Constant.TEXT_PORT) {
					Client.textSession = session;
				} else if (port == Constant.IMAGE_PORT) {
					Client.imageSession = session;
				} else if (port == Constant.HEARTBEAT_PORT) {
					Client.heartbeatSession = session;
				}
			}
			User user = new User();
			user.setHeader(Constant.LOGIN);
			user.setUser(account);
			user.setPassword(password);
			user.setTimeStamp(new Date().toString());
			user.setStatus(0);
			// send login message
			WriteFuture write = session.write(user);
			if (!write.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				Debug.println("failed to write: " + session.getRemoteAddress());
				return false;
			}
			if (!write.isWritten())
				return false;
			// read return message
			ReadFuture read = session.read();
			if (read.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				User retMsg = (User) read.getMessage();
				if (retMsg.getStatus().equals(1)) {
					Debug.println("login retrun message is correct!");
					return true;
				} else {
					Debug.println("login retrun message is not correct!");
					return false;
				}
			} else {
				Debug.println("failed to read: " + session.getRemoteAddress());
				return false;
			}
		} catch (Exception e) {
			logger.error("Error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
	
	private IoSession connectAgain(Integer port) {
		ConnectFuture conn = null;
		Debug.println("connect to port: " + port + " again!!");
		conn = Client.connector.connect(new InetSocketAddress(
				Constant.SERVER_HOST, port));
		if (conn.awaitUninterruptibly(Constant.CONNECT_OVERTIME)) {
			return conn.getSession();
		} else {
			Debug.println("failed to connect port: " + port);
			return null;
		}
		
		
	}
	
	/**
	 * 发送文字信息
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param message
	 * @return
	 */
	public Boolean sendMessage(IoSession session, String sender,
			String receiver, Integer type, String message) {
		try {
			Message msg = new Message();
			msg.setHeader(Constant.MESSAGE);
			msg.setSender(sender);
			msg.setReceiver(receiver);
			msg.setMessage(message);
			msg.setType(type);
			msg.setTimeStamp(new Date().toString());
			WriteFuture write = session.write(msg);
			if (!write.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				Debug.println("failed to write!!!");
				return false;
			}
			if (write.isWritten())
				return true;
			return false;
		} catch (Exception e) {
			logger.error("send message error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
	
	/**
	 * 发送心跳包
	 * @param account
	 * @return
	 */
	public Boolean sendHeartbeat(IoSession session, String account) {
		try {
			Heartbeat hb = new Heartbeat();
			hb.setAccount(account);
			hb.setHeader(Constant.HEARTBEAT);
			hb.setTimeStamp(new Date().toString());
			WriteFuture write = session.write(hb);
			if (!write.awaitUninterruptibly(Constant.MESSAGE_OVERTIME)) {
				Debug.println("failed to write!!!");
				return false;
			}
			if (write.isWritten())
				return true;
			return false;
		} catch (Exception e) {
			logger.error("send heartbeat error: " + e.getMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
	
	/**
	 * 发送图片
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param extra
	 * @param filePath
	 * @return
	 */
	public Boolean sendImage(IoSession session, String sender, 
			String receiver, String extra, String filePath) {
		try {
			Image img = new Image();
			img.setHeader(Constant.IMAGE);
			img.setSender(sender);
			img.setReceiver(receiver);
			img.setExtra(extra);
			img.setTimeStamp(new Date().toString());
			InputStream in = new FileInputStream(filePath);
			byte[] dst = ImageUtil.imageCompress(in, 0.9, 1.0);
			img.setImage(dst);
			WriteFuture write = session.write(img);
			if (!write.awaitUninterruptibly(Constant.IMAGE_OVERTIME)) {
				Debug.println("failed to write!!!");
				return false;
			}
			if (write.isWritten())
				return true;
			return false;
		} catch (Exception e) {
			logger.error("send image error: " + e.getLocalizedMessage());
			Debug.printStackTrace(e);
			return false;
		}
	}
}
