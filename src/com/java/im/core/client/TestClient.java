package com.java.im.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.java.im.constant.Constant;
import com.java.im.core.model.BytePacket;
import com.java.im.core.model.DataPacket;
import com.java.im.util.ConvertUtil;

public class TestClient {

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		String host = "127.0.0.1";// "app1.ufriend.cc";
		int port = 7777;
		Socket client = new Socket(host, port);
		OutputStream writer = client.getOutputStream();
		InputStream reader = client.getInputStream();
		DataPacket dp = new DataPacket();
		dp.setAccessToken("123456");
		dp.setContentType(Constant.CONTENT_TYPE_LOGIN);
		dp.setReceiver("server\n");
		dp.setSender("wjh");
		dp.setStatus("0");
		dp.setType(Constant.TYPE_LOGIN);
		BytePacket bp = dp.toBytePacket();
		writer.write(ConvertUtil.i2b(bp.getHeader().length));
		writer.write(bp.getHeader());
		writer.write(ConvertUtil.i2b(0));
		writer.flush();
		
		BytePacket ret = new BytePacket();
		byte[] intByte = new byte[4];
		
		// read header length: int
		reader.read(intByte, 0, 4);
		int length = ConvertUtil.b2i(intByte);
		System.out.println("header length: " + length);
		
		// read header: byte[length]
		byte[] header = new byte[length];
		reader.read(header, 0, length);
		ret.setHeader(header);
		System.out.println(ret.toDataPacket().getReceiver());
		
		// read body length: int
		reader.read(intByte, 0, 4);
		length = ConvertUtil.b2i(intByte);
		System.out.println("body length: " + length);
		
		// read body: byte[length]
		byte[] body = new byte[length];
		int count = 0;
		while (count < length) {
			byte[] tmp = new byte[length - count];
			int len = reader.read(tmp, 0, length - count);
			System.out.println("len: " + len);
			for (int i = 0; i < len; i++) {
				body[i + count] = tmp[i];
			}
			count += len;
		}
		
		writer.close();
		reader.close();
		client.close();
	}
}
