package com.java.im.core.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.session.IoSession;

import com.java.im.constant.GlobalResource;
import com.java.im.util.Debug;

public class Login {

	public Boolean login(String account, String accessToken, String address) {
		// need to validate the accessToken
		// need to add accessToken createFunction
		// ......
		// ......
		// login service
		if (GlobalResource.userMap.containsKey(account)) {
			IoSession session = (IoSession) GlobalResource.userMap.get(account);
			SocketAddress remoteAddr = session.getRemoteAddress();
			if (address == null) {
				Debug.println("address is null");
				GlobalResource.userMap.remove(account);
				return true;
			}
			String addrStr = ((InetSocketAddress) remoteAddr).getHostName();
			if (!addrStr.equalsIgnoreCase(address)) {
				Debug.println("user had login!");
				return false;
			}
		}
		return true;
	}
}
