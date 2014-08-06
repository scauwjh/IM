package com.java.mina.api;

import com.java.mina.constant.GlobalResource;

public class API {
	
	/**
	 * 服务器总在线人数
	 * @return
	 */
	public static Integer onlineCount() {
		return GlobalResource.userMap.size();
	}
	
	/**
	 * 检查用户是否在线
	 * @param account
	 * @return
	 */
	public static Boolean ifOnline(String account) {
		return GlobalResource.userMap.containsKey(account);
	}
}
