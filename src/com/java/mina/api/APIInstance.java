package com.java.mina.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

import com.java.mina.constant.Constant;
import com.java.mina.constant.GlobalResource;
import com.java.mina.core.model.Heartbeat;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.util.Debug;
import com.java.mina.util.ImageUtil;

public class APIInstance implements API {
	
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
		User user = new User();
		user.setHeader(Constant.LOGIN);
		user.setUser(account);
		user.setPassword(password);
		user.setTimeStamp(new Date().toString());
		user.setStatus(0);
		session.write(user).awaitUninterruptibly();
		ReadFuture read = session.read();
		if (read.awaitUninterruptibly(Constant.LOGIN_OVEROUT)) {
			User retMsg = (User) read.getMessage();
			if (retMsg.getStatus().equals(1)) {
				return true;
			} else {
				return false;
			}
		} else {
			Debug.println("Failed to connect");
			return false;
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
		Message msg = new Message();
		msg.setHeader(Constant.SEND);
		msg.setSender(sender);
		msg.setReceiver(receiver);
		msg.setMessage(message);
		msg.setType(type);
		msg.setTimeStamp(new Date().toString());
		return session.write(msg).isWritten();
	}
	
	/**
	 * 发送心跳包
	 * @param session
	 * @param account
	 * @return
	 */
	public Boolean sendHeartbeat(IoSession session, String account) {
		Heartbeat hb = new Heartbeat();
		hb.setAccount(account);
		hb.setHeader(Constant.HEARTBEAT);
		hb.setTimeStamp(new Date().toString());
		return session.write(hb).isWritten();
	}
	
	/**
	 * 发送图片
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param filePath
	 * @return
	 */
	public Boolean sendImage(IoSession session, String sender, 
			String receiver, String filePath) {
		Image img = new Image();
		img.setHeader(Constant.IMAGE);
		img.setSender(sender);
		img.setReceiver(receiver);
		img.setTimeStamp(new Date().toString());
		try {
			InputStream in = new FileInputStream(filePath);
			byte[] dst = ImageUtil.imageCompress(in, 0.9, 1.0);
			img.setImage(dst);
			return session.write(img).isWritten();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
