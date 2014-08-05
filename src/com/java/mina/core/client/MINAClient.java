package com.java.mina.core.client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Scanner;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.java.mina.constant.Constant;
import com.java.mina.core.filter.MyCharsetCodecFactory;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.util.ImageUtil;

public class MINAClient extends Thread {
	
	private SocketConnector connector;
	
	private IoSession session;
	
	private ConnectFuture future;
	
	private final static Long TIMEOUT = 3000L;
	
	private final static Integer PORT = 9999;
	
	private final static String ADDRESS = "127.0.0.1";
	
	private final static String CHARSET = "UTF-8";
	
	
	public void client() {
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		String sender = in.next();
		
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(TIMEOUT);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory(CHARSET)));
		connector.setHandler(new ClientHandler());
		future = connector.connect(new InetSocketAddress(ADDRESS, PORT));
		future.awaitUninterruptibly();
		session = future.getSession();
		
//		login(sender, "123456");
		
		while(true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit"))
				break;
			String message = in.next();
			if (message.equals("image")) {
				String path = "C:\\Users\\asus\\Desktop\\123.png";
				try {
					sendImage(sender, receiver, path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sendMessage(sender, receiver, message);
		}
		in.close();
	}
	
	public void login(String account, String password) {
		User user = new User();
		user.setHeader(Constant.LOGIN);
		user.setUser(account);
		user.setPassword(password);
		user.setTimeStamp(new Date().toString());
		session.write(user);
	}
	
	public void sendMessage(String sender, String receiver, String message) {
		Message msg = new Message();
		msg.setHeader(Constant.SEND);
		msg.setSender(sender);
		msg.setReceiver(receiver);
		msg.setMessage(message);
		msg.setTimeStamp(new Date().toString());
		session.write(msg);
	}
	
	public void sendImage(String sender,String receiver, String filePath) 
			throws Exception {
		Image img = new Image();
		img.setHeader(Constant.IMAGE);
		img.setSender(sender);
		img.setReceiver(receiver);
		img.setTimeStamp(new Date().toString());
		InputStream in = new FileInputStream(filePath);
		byte[] dst = ImageUtil.imageCompress(in, 0.9, 1.0);
		img.setImage(dst);
		session.write(img);
	}
	
	@Override
	public void run() {
		client();
	}
	
	public static void main(String[] args) {
		new MINAClient().start();
	}
}
