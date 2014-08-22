package com.java.mina.core.model;

import java.io.UnsupportedEncodingException;

import com.java.mina.constant.Constant;
import com.java.mina.util.Debug;

public class DataPacket {
	
	private String type;
	
	private String sender;
	
	private String receiver;
	
	private String accessToken;
	
	private String status;
	
	private String contentType;
	
	private String timeStamp;
	
	private String parameters;

	private byte[] body;
	
	public BytePacket toBytePacket() {
		try { 
			BytePacket packet = new BytePacket();
			StringBuffer sb = new StringBuffer();
			sb.append(type + "\n");
			sb.append(sender + "\n");
			sb.append(receiver + "\n");
			sb.append(accessToken + "\n");
			sb.append(status + "\n");
			sb.append(contentType + "\n");
			sb.append(timeStamp + "\n");
			sb.append(parameters + "\n\n");
			packet.setHeader(sb.toString().getBytes(Constant.CHARSET));
			packet.setBody(body);
			return packet;
		} catch (UnsupportedEncodingException e) {
			Debug.printStackTrace(e);
			return null;
		}
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}


	public String getAccessToken() {
		return accessToken;
	}


	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getParameters() {
		return parameters;
	}


	public void setParameters(String parameters) {
		this.parameters = parameters;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
