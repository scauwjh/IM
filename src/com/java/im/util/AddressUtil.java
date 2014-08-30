package com.java.im.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.session.IoSession;

public class AddressUtil {
	
	public static Integer getLocalPort(IoSession session) {
		SocketAddress socketAddress = session.getLocalAddress();
		if (socketAddress == null)
			return -1;
		InetSocketAddress address = (InetSocketAddress) socketAddress;
		return address.getPort();
	}
	
	public static Integer getRemotePort(IoSession session) {
		SocketAddress socketAddress = session.getRemoteAddress();
		if (socketAddress == null)
			return -1;
		InetSocketAddress address = (InetSocketAddress) socketAddress;
		return address.getPort();
	}
}
