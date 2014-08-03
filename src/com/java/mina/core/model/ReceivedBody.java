package com.java.mina.core.model;

import java.io.Serializable;

public class ReceivedBody implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String method;
	
	private String param1;
	
	private String param2;

	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}
}
