package com.java.mina.core.client;

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
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;

public class MINAClient extends Thread {
	
	private SocketConnector connector;
	
	private IoSession session;
	
	private ConnectFuture future;
	
	private final static Long TIMEOUT = 3000L;
	
	private final static Integer PORT = 9999;
	
	private final static String ADDRESS = "127.0.0.1";
	
	
	public void client() {
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		String sender = in.next();
		
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(TIMEOUT);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new MyCharsetCodecFactory()));
		connector.setHandler(new ClientHandler());
		future = connector.connect(new InetSocketAddress(ADDRESS, PORT));
		future.awaitUninterruptibly();
		session = future.getSession();
		
		login(sender, "123456");
		
		while(true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit"))
				break;
			String message = in.next();
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
	
	@Override
	public void run() {
		client();
	}
	
	public static void main(String[] args) {
		new MINAClient().start();
	}
}
