package com.java.mina.core.service;

import org.apache.mina.core.session.IoSession;

import com.java.mina.constant.Constant;

public class Login {

	public void login(IoSession session, String account, String password) {
		session.setAttribute(Constant.ACCOUNT, account);
		// Processing offline message
	}
}
