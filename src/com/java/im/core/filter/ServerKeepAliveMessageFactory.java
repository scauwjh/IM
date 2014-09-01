package com.java.im.core.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.model.DataPacket;

public class ServerKeepAliveMessageFactory implements KeepAliveMessageFactory {

	public final static Logger logger = LoggerFactory
			.getLogger(ServerKeepAliveMessageFactory.class);

	@Override
	public boolean isRequest(IoSession session, Object message) {
		if (message instanceof DataPacket) {
			DataPacket hb = (DataPacket) message;
			if (hb.getType().equals(Constant.TYPE_HEARTBEAT)) {
				logger.info("Reuqest heartbeat received from: "
						+ session.getRemoteAddress());
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		logger.info("Response heartbeat send to: "
				+ session.getRemoteAddress());
		DataPacket hb = new DataPacket();
		hb.setType(Constant.TYPE_HEARTBEAT);
		return hb;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		return false;// 被动型心跳机制无请求，无须关注，返回false
	}

	@Override
	public Object getRequest(IoSession session) {
		return null; // 被动型心跳机制无请求，返回null
	}
}
