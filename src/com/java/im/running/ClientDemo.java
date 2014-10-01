package com.java.im.running;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.Scanner;

import org.apache.mina.core.session.IoSession;

import com.java.im.constant.Constant;
import com.java.im.core.client.Client;
import com.java.im.core.model.DataPacket;
import com.java.im.util.PropertiesUtil;
import com.java.im.util.StringUtil;

public class ClientDemo extends Client {
	
	public static Integer errCount = 0;
	
	public static String account;
	
	public static String accessToken;
	
	public static ClientDemo client;
	
	
	/**
	 * 获取配置
	 */
	static {
		String path = ClientDemo.class.getResource("/").getPath() 
				+ "imconfigure.properties";
		Map<String, String> map = PropertiesUtil.getProperties(path);
		Constant.SERVER_HOST = map.get("serverHost");
		Constant.TEXT_PORT = Integer.valueOf(map.get("textPort"));
		Constant.IMAGE_PORT = Integer.valueOf(map.get("imagePort"));
		Constant.SERVER_BUFFER_SIZE = Integer.valueOf(map.get("bufferSize"));
		Constant.SERVER_CACHE_SIZE = Integer.valueOf(map.get("cacheSize"));
		Constant.IS_DEBUG = map.get("isDebug").equals("true");
	}
	
	/**
	 * 重写数据接收的方法
	 */
	@Override
	public void messageHandler(Object message) {
		try {
			DataPacket packet = (DataPacket) message;
			if (!packet.getType().equals(Constant.TYPE_SEND))
				return;
			System.out.println("message received form: " + packet.getSender());
			System.out.println("message status: " + packet.getStatus());
			System.out.println("Content type: " + packet.getContentType());
			System.out.println("TimeStamp: " + packet.getTimeStamp());
			if (packet.getContentType().equals(Constant.CONTENT_TYPE_IMAGE)) {
				String file = "C:\\Users\\asus\\Desktop\\rec-(" + account + ")-from-(" + packet.getSender() + ")" 
						+ StringUtil.randString(5)  + ".jpg";
				FileOutputStream out = new FileOutputStream(file);
				out.write(packet.getBody());
				out.close();
			} else if (packet.getContentType().equals(Constant.CONTENT_TYPE_MESSAGE)) {
				String body = new String(packet.getBody(), Constant.CHARSET);
				System.out.println("Received message: " + body);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重写session关闭的方法
	 */
	@Override
	public void closeSession(IoSession session) {
		System.out.println("!!!!!session is closed!!!!");
		int count = 0;
		while (true) {
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (client.login(session, account, accessToken)) {
				break;
			}
			System.out.println("login again: " + ++count);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		client = new ClientDemo();
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		account = in.next();
		accessToken = "123456";
		// login
		if (!client.login(account, accessToken)) {
			System.out.println("login failed!");
			in.close();
			client.close();
			return;
		}
		
		// send message service
		while(true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit")) {
				System.out.println("!!!!!!!!!!!!error count: " + errCount);
				break;
			}
			String message = in.next();
			if (message.equals("image")) {
				String path = "C:\\Users\\asus\\Desktop\\tmp\\123.png";
				// use multiple thread to finish the service
				client.sendImage(account, receiver, accessToken, "params", path);
				continue;
			}
			if (message.equals("close")) {
				continue;
			}
			if (message.equals("init")) {
				client.login(account, accessToken);
				continue;
			}
			client.sendMessage(account, receiver, accessToken, "params", message);
		}
		in.close();
	}
}
