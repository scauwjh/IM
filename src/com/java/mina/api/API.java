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
	 * @param connector
	 * @param portType 0 is text, 1 is image
	 * @param account
	 * @param password
	 * @return
	 */
	public Boolean login(IoSession session,int portType,
			String account, String password);
	
	/**
	 * 发送文字信息
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param type
	 * @param message
	 * @return
	 */
	public Boolean sendMessage(IoSession session, String sender,
			String receiver, Integer type, String message);
	
	/**
	 * 发送心跳包
	 * @param session
	 * @param account
	 * @return
	 */
	public Boolean sendHeartbeat(IoSession session, String account);
	
	/**
	 * 发送图片
	 * @param session
	 * @param sender
	 * @param receiver
	 * @param filePath
	 * @return
	 */
	public Boolean sendImage(IoSession session, String sender, 
			String receiver, String filePath);
	
}
