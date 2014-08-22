package com.java.mina.load;

import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;

import com.java.mina.constant.Constant;
import com.java.mina.core.client.Client;
import com.java.mina.core.model.DataPacket;
import com.java.mina.util.StringUtil;

public class ClientDemo extends Client {
	
	public static Integer errCount = 0;
	
	public static String account;
	
	public static String password;
	
	public static ClientDemo client;
	
	
	/**
	 * 重写数据接收的方法
	 */
	@Override
	public void messageHandler(Object message) {
		try {
			DataPacket packet = (DataPacket) message;
			System.out.println("Image received form: " + packet.getSender());
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
		client.login(session, account, password);
	}
	
	
	public static void main(String[] args) throws Exception {
		client = new ClientDemo();
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		account = in.next();
		password = "123456";
		// login
		if (!client.login(account, password)) {
			System.out.println("login failed!");
			in.close();
			return;
		}
		
		TimerTask task = new TimerTask() {  
	        public void run() {
	        	if (client.sendHeartbeat(account, password))
					System.out.println("beat succeed!");
				else {
					errCount ++;
					System.out.println("beat failed!");
				}
	        }     
	    };
		Timer timer = new Timer(true);
		timer.schedule(task, 2000, 30000);
		
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
				client.sendImage(account, receiver, password, "params", path);
				continue;
			}
			if (message.equals("close")) {
				heartbeatSession.close(false);
				Thread.sleep(3000);
				continue;
			}
			if (message.equals("init")) {
				client.login(account, password);
				continue;
			}
			client.sendMessage(account, receiver, password, "params", message);
		}
		in.close();
	}
}
