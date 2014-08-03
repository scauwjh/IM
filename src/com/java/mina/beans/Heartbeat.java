package com.java.mina.beans;

import java.io.Serializable;

public class Heartbeat implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
