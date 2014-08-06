package com.java.mina.util;

import java.net.InetSocketAddress;

import org.apache.mina.core.session.IoSession;

public class AddressUtil {
	
	public static Integer getPort(IoSession session) {
		InetSocketAddress address = (InetSocketAddress) session.getLocalAddress();
		return address.getPort();
	}
}
