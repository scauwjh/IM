package com.java.im.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import org.apache.mina.core.session.IoSession;

import com.java.im.constant.Constant;
import com.java.im.core.client.Client;
import com.java.im.core.model.DataPacket;
import com.java.im.util.Debug;
import com.java.im.util.ImageUtil;
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
				+ "im.properties";
		File file = new File(path);
		if (file.exists()) {
			Map<String, String> map = PropertiesUtil.getProperties(path);
			Constant.SERVER_HOST = map.get("serverHost");
			Constant.TEXT_PORT = Integer.valueOf(map.get("textPort"));
			Constant.IMAGE_PORT = Integer.valueOf(map.get("imagePort"));
			Constant.SERVER_BUFFER_SIZE = Integer
					.valueOf(map.get("bufferSize"));
			Constant.SERVER_CACHE_SIZE = Integer.valueOf(map.get("cacheSize"));
			Constant.IS_DEBUG = map.get("isDebug").equals("true");
			Debug.println(Constant.DEBUG_INFO,
					"Read properties from configure file of customer");
		}
	}

	/**
	 * 重写数据接收的方法
	 */
	@Override
	public void messageHandler(DataPacket packet) {
		try {
			System.out.println("message type: " + packet.getType());
			System.out.println("message received form: " + packet.getSender());
			System.out.println("message status: " + packet.getStatus());
			System.out.println("Content type: " + packet.getContentType());
			System.out.println("TimeStamp: " + packet.getTimeStamp());
			if (packet.getContentType().equals(Constant.CONTENT_TYPE_IMAGE)) {
				String file = "C:\\Users\\kei\\Desktop\\rec-(" + account
						+ ")-from-(" + packet.getSender() + ")"
						+ StringUtil.randString(5) + ".jpg";
				FileOutputStream out = new FileOutputStream(file);
				out.write(packet.getBody());
				out.close();
			} else if (packet.getContentType().equals(
					Constant.CONTENT_TYPE_MESSAGE)) {
				String body = new String(packet.getBody(), Constant.CHARSET);
				System.out.println("Received message: " + body);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void returnHandler(DataPacket packet) {
		System.out.println("#####" + packet.getType() + " " + packet.getAccessToken());
		System.out.println("##### message is sent, and return status is: "
						+ packet.getStatus() + ", id is: "
						+ packet.getIdentification());
	}

	/**
	 * 重写session关闭的方法
	 */
	@Override
	public void closeSession(IoSession session) {
		System.out.println("session is closed!");
		int count = 0;
		while (true) {
			try {
				Thread.sleep(2000L);
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
		if (!client.initClient(null)) {
			System.out.println("Failed to init client!");

			return;
		}
		Scanner in = new Scanner(System.in);
		System.out.println("enter a name for user: ");
		account = in.next();
		accessToken = "123456";
		// login
		if (!client.initLogin(account, accessToken)) {
			System.out.println("login failed!");
			in.close();
			client.close();
			return;
		}

		// send message service
		while (true) {
			System.out.println("enter name message or exit: ");
			String receiver = in.next();
			if (receiver.equals("exit")) {
				System.out.println("----error count: " + errCount);
				break;
			}
			String message = in.next();
			if (message.equals("image")) {
				String path = "C:\\Users\\kei\\Desktop\\友换\\123.png";
				// use multiple thread to finish the service
				InputStream inputStream;
				inputStream = new FileInputStream(path);
				byte[] dst = ImageUtil.imageCompress(inputStream, 0.9, 1.0);
				DataPacket dp = client.sendImage(account, receiver,
						accessToken, "params", dst);
				System.out.println("----- image sent with id: "
						+ dp.getIdentification());
				continue;
			}
			if (message.equals("close")) {
				continue;
			}
			if (message.equals("init")) {
				client.initLogin(account, accessToken);
				continue;
			}
			DataPacket dp = client.sendMessage(account, receiver, accessToken,
					"params", message);
			System.out.println("----- message sent with id: "
					+ dp.getIdentification());
		}
		in.close();
	}
}
