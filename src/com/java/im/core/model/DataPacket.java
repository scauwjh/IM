package com.java.im.core.model;

import java.io.UnsupportedEncodingException;

import com.java.im.constant.Constant;
import com.java.im.util.Debug;

/**
 * <b>数据包</b>
 * @author wjh
 * @version 2014-09-27
 */
public class DataPacket {
	/**
	 * <b>数据类型，用于服务器判断解析</b>
	 * <p>value: loginType</p>
	 * <p>value: sendType</p>
	 * <p>value: heartbeatType</p>
	 */
	private String type;
	/**
	 * <b>消息发送者</b>
	 */
	private String sender;
	/**
	 * <b>消息接受者</b>
	 */
	private String receiver;
	/**
	 * <b>授权凭证（暂时没有相应的处理逻辑）</b>
	 */
	private String accessToken;
	/**
	 * <b>登录状态，服务器返回</b>
	 * <p>0 is login failed</p>
	 * <p>1 is login succeed</p>
	 */
	private String status;
	/**
	 * <b>Body数据类型，用于客户端数据解析</b>
	 * <p>value: contentTypeMessage</p>
	 * <p>value: contentTypeImage</p>
	 * <b>以下两种可以不设置，
	 * 客户端不需根据这个字段来进行判断：</b>
	 * <p>value: contentTypeHeartbeat</p>
	 * <p>value: contentTypeLogin</p>
	 */
	private String contentType;
	/**
	 * <b>时间戳</b>
	 */
	private String timeStamp;
	/**
	 * <b>客户端自定义参数字段</b>
	 * <p>例如客户端要传输一段语音，<br>
	 * 然后还要附带语音的长度等信息，<br>
	 * 就可以自定义parameters格式（如json）传输</p>
	 */
	private String parameters;
	/**
	 * <b>客户端消息主体</b>
	 * <p>可以为文字消息（string转成byte[]）<br>
	 * 或者语音、图片等（byte[]）</p>
	 */
	private byte[] body;
	
	/**
	 * <b>数据包转字节包</b>
	 * @return BytePacket or null
	 */
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
			Debug.printStackTrace(Constant.DEBUG_WARN, e);
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
