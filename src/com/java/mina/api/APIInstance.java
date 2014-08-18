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
	 * @param account
	 * @param password
	 * @return
	 */
	public Boolean login(String account, String password) {
		Debug.println("------login begin------");
		ConnectFuture conn = null;
		ReadFuture read = null;
		if (Client.textSession.isClosing()) {
			Debug.println("connect text port again!!");
			conn = Client.connector.connect(new InetSocketAddress(
					Constant.REMOTE_ADDRESS, Constant.TEXT_PORT));
			conn.awaitUninterruptibly();
			Client.textSession = conn.getSession();
		}
		if (Client.imageSession.isClosing()) {
			Debug.println("connect image port again!!");
			conn = Client.connector.connect(new InetSocketAddress(
					Constant.REMOTE_ADDRESS, Constant.IMAGE_PORT));
			conn.awaitUninterruptibly();
			Client.imageSession = conn.getSession();
		}
		// send login packet
		// text
		User user = new User();
		user.setHeader(Constant.LOGIN);
		user.setUser(account);
		user.setPassword(password);
		user.setTimeStamp(new Date().toString());
		user.setStatus(0);
		WriteFuture write = Client.textSession.write(user).awaitUninterruptibly();
		if (!write.isWritten())
			return false;
		read = Client.textSession.read();
		if (read.awaitUninterruptibly(Constant.LOGIN_OVERTIME)) {
			User retMsg = (User) read.getMessage();
			if (retMsg.getStatus().equals(1)) {
				Debug.println("text session login succeed!");
			} else {
				Debug.println("login failed!");
				return false;
			}
		} else {
			Debug.println("Failed to connect");
			return false;
		}
		// image
		write = Client.imageSession.write(user).awaitUninterruptibly();
		if (!write.isWritten())
			return false;
		read = Client.imageSession.read();
		if (read.awaitUninterruptibly(Constant.LOGIN_OVERTIME)) {
			User retMsg = (User) read.getMessage();
			if (retMsg.getStatus().equals(1)) {
				Debug.println("image session login succeed!");
			} else {
				Debug.println("login failed!");
				return false;
			}
		} else {
			Debug.println("Failed to connect");
			return false;
		}
		// end return
		Debug.println("------login end------");
		return true;
		
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
		Message msg = new Message();
		msg.setHeader(Constant.SEND);
		msg.setSender(sender);
		msg.setReceiver(receiver);
		msg.setMessage(message);
		msg.setType(type);
		msg.setTimeStamp(new Date().toString());
		WriteFuture write = session.write(msg).awaitUninterruptibly();
		return write.isWritten();
	}
	
	/**
	 * 发送心跳包
	 * @param account
	 * @return
	 */
	public Boolean sendHeartbeat(String account) {
		Heartbeat hb = new Heartbeat();
		hb.setAccount(account);
		hb.setHeader(Constant.HEARTBEAT);
		hb.setTimeStamp(new Date().toString());
		WriteFuture write = Client.heartbeatSession.write(hb).awaitUninterruptibly();
		if (!write.isWritten())
			return false;
		ReadFuture read = Client.heartbeatSession.read();
		if (read.awaitUninterruptibly(Constant.HEARTBEAT_OVERTIME)) {
			Heartbeat retMsg = (Heartbeat) read.getMessage();
			if (retMsg.getAccount().equals(account)) {
				Debug.println("heartbeat received is correct!");
			} else {
				Debug.println("heartbeat received is not correct!");
				return false;
			}
		}
		return true;
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
		if (session.isClosing()) {
			Debug.println("send image is close!!!");
		}
		Image img = new Image();
		img.setHeader(Constant.IMAGE);
		img.setSender(sender);
		img.setReceiver(receiver);
		img.setExtra(extra);
		img.setTimeStamp(new Date().toString());
		try {
			InputStream in = new FileInputStream(filePath);
			byte[] dst = ImageUtil.imageCompress(in, 0.9, 1.0);
			img.setImage(dst);
			return session.write(img).awaitUninterruptibly().isWritten();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
