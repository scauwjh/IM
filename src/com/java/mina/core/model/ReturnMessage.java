package com.java.mina.core.model;

import java.io.Serializable;

public class ReturnMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer code;
	
	private String msg;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
