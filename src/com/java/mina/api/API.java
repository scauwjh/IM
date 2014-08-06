package com.java.mina.api;

import org.apache.mina.core.session.IoSession;

public interface API {
	
	/**
	 * 服务器总在线人数
	 * @return
	 */
	public Integer onlineCount();
	
	/**
	 * 检查用户是否在线
	 * @param account
	 * @return
	 */
	public Boolean ifOnline(String account);
	
	/**
	 * 发送登录信息
	 * @param session
	 * @param account
	 * @param password
	 */
	public void login(IoSession session, String account, String password);
	
	/**
	 * 发送文字信息
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param message
	 */
	public void sendMessage(IoSession session, String sender,
			String receiver, String message);
	
	/**
	 * 发送图片
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param filePath
	 * @return
	 */
	public boolean sendImage(IoSession session, String sender, 
			String receiver, String filePath);
	
}
