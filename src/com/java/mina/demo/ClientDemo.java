package com.java.mina.demo;

import java.io.FileOutputStream;
import java.util.Scanner;

import com.java.mina.api.API;
import com.java.mina.api.APIInstance;
import com.java.mina.core.client.Client;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.util.StringUtil;

public class ClientDemo extends Client {
	
	/**
	 * 重写文字消息接收的方法
	 */
	@Override
	public void messageReceived(Message message) {
		Message msg = (Message) message;
		System.out.println("message received form: " + msg.getSender());
		System.out.println("timeStamp: " + msg.getTimeStamp());
		System.out.println("type: " + msg.getType());
		System.out.println("message: " + msg.getMessage());
	}
	
	/**
	 * 重写图片接收的方法
	 */
	@Override
	public void imageReceived(Image message) {
		Image msg = (Image) message;
		System.out.println("image received form: " + msg.getSender());
		System.out.println("extra: " + msg.getExtra());
		System.out.println("timeStamp: " + msg.getTimeStamp());
		String file = "C:\\Users\\asus\\Desktop\\rec" + StringUtil.randString(5)  + ".jpg";
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(msg.getImage());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重写string接收的方法
	 */
	@Override
	public void stringReceived(String message) {
		System.out.println("received retMsg from server: " + message);
	}
	
	
	public static void main(String[] args) {
		ClientDemo client = new ClientDemo();
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		String sender = in.next();
		String password = "123456";
		// login
		client.login(sender, password);
		
		// send message service
		while(true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit"))
				break;
			String message = in.next();
			if (message.equals("image")) {
				String path = "C:\\Users\\asus\\Desktop\\tmp\\123.png";
				// use multiple thread to finish the service
				client.sendImage(sender, receiver, "extra", path);
				continue;
			}
			if (message.equals("beat")) {
				// send heartbeat
				if (client.sendHeartbeat(sender))
					System.out.println("beat succeed!");
				else System.out.println("beat failed!");
				continue;
			}
			if (message.equals("check")) {
				API api = new APIInstance();
				if (api.ifOnline(receiver))
					System.out.println(receiver + " is online");
				else
					System.out.println(receiver + " is not online");
				continue;
			}
			if (message.equals("how")) {
				API api = new APIInstance();
				System.out.println("online count: " + api.onlineCount());
				continue;
			}
			client.sendMessage(sender, receiver, 1, message);
		}
		in.close();
	}
}
