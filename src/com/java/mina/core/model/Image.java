package com.java.mina.core.model;

import java.io.Serializable;

public class Image implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String header;
	
	private String sender;
	
	private String receiver;
	
	private byte[] image;
	
	private String timeStamp;


	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public Integer getSize() {
		return header.getBytes().length + sender.getBytes().length
				+ receiver.getBytes().length + image.length 
				+ timeStamp.getBytes().length;
	}
	
}
