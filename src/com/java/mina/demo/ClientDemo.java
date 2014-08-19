package com.java.mina.demo;

import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.java.mina.api.API;
import com.java.mina.api.APIInstance;
import com.java.mina.core.client.Client;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.util.StringUtil;

public class ClientDemo extends Client {
	
	public static Integer count = 0;
	
	public static Integer errCount = 0;
	
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
		final ClientDemo client = new ClientDemo();
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		final String sender = in.next();
		final String password = "123456";
		// login
		if (!client.login(sender, password)) {
			System.out.println("login failed!");
			in.close();
			return;
		}
		
		TimerTask task = new TimerTask() {  
	        public void run() {
	        	if (count == 0 && client.sendHeartbeat(sender))
					System.out.println("beat succeed!");
				else {
					errCount ++;
					System.out.println("beat failed!");
					if (count++ > 4) {
						if (client.login(sender, password)) {
							System.out.println("login succeed!");
							count = 0;
						} else {
							System.out.println("login failed!");
						}
					}
				}
	        }     
	    };
		Timer timer = new Timer(true);
		timer.schedule(task, 2000, 5000);
		
		// send message service
		while(true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit")) {
				timer.cancel();
				System.out.println("!!!!!!!!!!!!error count: " + errCount);
				break;
			}
			String message = in.next();
			if (message.equals("image")) {
				String path = "C:\\Users\\asus\\Desktop\\tmp\\123.png";
				// use multiple thread to finish the service
				client.sendImage(sender, receiver, "extra", path);
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
