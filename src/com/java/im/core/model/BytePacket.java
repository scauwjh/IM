package com.java.im.core.model;

import java.io.UnsupportedEncodingException;

import com.java.im.constant.Constant;
import com.java.im.util.Debug;

/**
 * <b>数据包</b>
 * @author wjh
 * @version 2014-09-27
 */
public class BytePacket {
	
	private byte[] header;
	
	private byte[] body;

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public byte[] getHeader() {
		return header;
	}

	public void setHeader(byte[] header) {
		this.header = header;
	}
	
	/**
	 * <b>字节包解析，转数据包</b>
	 * @return DatePacket or null
	 */
	public DataPacket toDataPacket() {
		try {
			String header = new String(this.header, Constant.CHARSET);
			DataPacket packet = new DataPacket();
			// type
			int begin = 0;
			int index = header.indexOf('\n');
			packet.setType(header.substring(begin, index));
			// sender
			begin = index;
			index = header.indexOf('\n', begin + 1);
			String tmp = header.substring(begin + 1, index);
			packet.setSender(tmp);
			Debug.println("sender in toDataPacket: " + tmp);
			// receiver
			begin = index;
			index = header.indexOf('\n', begin + 1);
			packet.setReceiver(header.substring(begin + 1, index));
			// access token
			begin = index;
			index = header.indexOf('\n', begin + 1);
			packet.setAccessToken(header.substring(begin + 1, index));
			// status
			begin = index;
			index = header.indexOf('\n', begin + 1);
			packet.setStatus(header.substring(begin + 1, index));
			// content type
			begin = index;
			index = header.indexOf('\n', begin + 1);
			packet.setContentType(header.substring(begin + 1, index));
			// time stamp
			begin = index;
			index = header.indexOf('\n', begin + 1);
			packet.setTimeStamp(header.substring(begin + 1, index));
			// parameters
			begin = index;
			index = header.indexOf('\n', begin + 1);
			packet.setParameters(header.substring(begin + 1, index));
			// body
			packet.setBody(body);
			return packet;
		} catch (UnsupportedEncodingException e) {
			Debug.printStackTrace(e);
			return null;
		}
	}
	
	/*
	public static void main(String[] args) {
		DataPacket decode = new DataPacket();
		decode.setType("type");
		decode.setSender("sender");
		decode.setReceiver("receiver");
		decode.setAccessToken("token");
		decode.setStatus("status");
		decode.setContentType("content-type");
		decode.setTimeStamp("timeStamp");
		decode.setParameters("params");
		decode.setBody(null);
		BytePacket encode = decode.toBytePacket();
		decode = encode.toDataPacket();
		JSONObject json = JSONObject.fromObject(decode);
		System.out.println(json);
	}
	*/
}
