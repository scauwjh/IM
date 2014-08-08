package com.java.mina.core.model;

import java.io.Serializable;

public class Heartbeat implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String header;
	
	private String account;
	
	private String timeStamp;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
