package com.java.mina.beans;

import java.io.Serializable;

import org.apache.mina.core.session.IoSession;

public class Session implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * IoSession
	 */
	private IoSession session;
	/**
	 * uid
	 */
	private Long uid;
	/**
	 * 绑定账户
	 */
	private String account;
	/**
	 * 绑定时间
	 */
	private Long bindTime;
	/**
	 * 心跳
	 */
	private Long hartbeat;

	
	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getHartbeat() {
		return hartbeat;
	}

	public void setHartbeat(Long hartbeat) {
		this.hartbeat = hartbeat;
	}

	public Long getBindTime() {
		return bindTime;
	}

	public void setBindTime(Long bindTime) {
		this.bindTime = bindTime;
	}
}
