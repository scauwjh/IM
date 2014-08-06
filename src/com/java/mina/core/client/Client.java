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
import com.java.mina.util.Debug;
import com.java.mina.util.ImageUtil;

public class Client extends Thread {
	
	private SocketConnector connector;
	
	public void client() {
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		String sender = in.next();
		
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(Constant.CONNECT_TIMEOUT);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory(Constant.CHARSET)));
		connector.setHandler(new ClientHandler());
		
		// connect to image port
		ConnectFuture textFuture = connector.connect(new InetSocketAddress(
				Constant.REMOTE_ADDRESS, Constant.TEXT_PORT));
		textFuture.awaitUninterruptibly();
		IoSession font = textFuture.getSession();
		
		// connect to image port
		ConnectFuture imageFuture = connector.connect(new InetSocketAddress(
				Constant.REMOTE_ADDRESS, Constant.IMAGE_PORT));
		imageFuture.awaitUninterruptibly();
		IoSession image = imageFuture.getSession();
		
		// login
		login(font, sender, "123456");
		login(image, sender, "123456");
		
		// send message service
		while(true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit"))
				break;
			String message = in.next();
			if (message.equals("image")) {
				String path = "C:\\Users\\asus\\Desktop\\123.png";
				// use multiple thread to finish the service
				sendImage(image, sender, receiver, path);
				continue;
			}
			sendMessage(font, sender, receiver, message);
		}
		in.close();
	}
	
	public void login(IoSession session, String account, String password) {
		User user = new User();
		user.setHeader(Constant.LOGIN);
		user.setUser(account);
		user.setPassword(password);
		user.setTimeStamp(new Date().toString());
		session.write(user);
	}
	
	public void sendMessage(IoSession session, String sender,
			String receiver, String message) {
		Debug.println("send a message!");
		Message msg = new Message();
		msg.setHeader(Constant.SEND);
		msg.setSender(sender);
		msg.setReceiver(receiver);
		msg.setMessage(message);
		msg.setTimeStamp(new Date().toString());
		session.write(msg);
	}
	
	public void sendImage(IoSession session, String sender, 
			String receiver, String filePath) {
		Image img = new Image();
		img.setHeader(Constant.IMAGE);
		img.setSender(sender);
		img.setReceiver(receiver);
		img.setTimeStamp(new Date().toString());
		try {
			InputStream in = new FileInputStream(filePath);
			byte[] dst = ImageUtil.imageCompress(in, 0.9, 1.0);
			img.setImage(dst);
			session.write(img);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		client();
	}
	
	public static void main(String[] args) {
		new Client().start();
	}
}
