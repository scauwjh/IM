package com.java.im.core.client.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.client.Client;
import com.java.im.core.model.DataPacket;

public class ClientHeartbeat extends Thread {

	public static final Logger logger = LoggerFactory.getLogger(ClientHeartbeat.class);
	
	private boolean stop;
	
	@Override
	public void run() {
		stop = false;
		DataPacket dp = new DataPacket();
		dp.setType(Constant.TYPE_HEARTBEAT);
		while (true) {
			if (stop)
				break;
			try {
				Thread.sleep(Constant.CLIENT_HEARTBEAT_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (Client.textSession.write(dp).awaitUninterruptibly(Constant.HEARTBEAT_TIMEOUT)) {
				logger.info("Heartbeat of text session is sent");
			} else {
				logger.warn("Heartbeat overtime at text session");
			}
			if (Client.imageSession.write(dp).awaitUninterruptibly(Constant.HEARTBEAT_TIMEOUT)) {
				logger.info("Heartbeat of image session is sent");
			} else {
				logger.warn("Heartbeat overtime at image session");
			}
		}
	}
	
	public void userStop() {
		stop = true;
	}
}
