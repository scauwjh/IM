package com.java.mina.core.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.java.mina.core.model.ReceivedBody;
import com.java.mina.util.Debug;
import com.java.mina.util.JsonUtil;

public class MINAClient extends Thread {
	
	private SocketConnector connector;
	
	private IoSession session;
	
	private ConnectFuture future;
	
	private final static Long TIMEOUT = 3000L;
	
	private final static Integer PORT = 9999;
	
	private final static String ADDRESS = "127.0.0.1";
	
	
	public void client() {
		Scanner in = new Scanner(System.in);
		Debug.println("enter a name for user: ");
		String fromUser = in.next();
		
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(TIMEOUT);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("UTF-8"))));
		connector.setHandler(new ClientHandler());
		future = connector.connect(new InetSocketAddress(ADDRESS, PORT));
		future.awaitUninterruptibly();
		session = future.getSession();
		
		sendMessage("login", fromUser, "123456");
		
		while(true) {
			Debug.println("enter name message or exit: ");
			String toUser = in.next();
			if (toUser.equals("exit"))
				break;
			String message = in.next();
			sendMessage("send", toUser, message);
		}
		in.close();
	}
	
	public void sendMessage(String method, String param1, String param2) {
		ReceivedBody body = new ReceivedBody();
		body.setMethod(method);
		body.setParam1(param1);
		body.setParam2(param2);
		session.write(JsonUtil.toJson(body));
	}
	
	@Override
	public void run() {
		client();
	}
	
	public static void main(String[] args) {
		new MINAClient().start();
	}
}
