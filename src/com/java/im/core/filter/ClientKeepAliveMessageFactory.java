package com.java.im.core.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;
import com.java.im.core.model.DataPacket;

public class ClientKeepAliveMessageFactory implements KeepAliveMessageFactory {

	public final static Logger logger = LoggerFactory.getLogger(ClientKeepAliveMessageFactory.class);

	@Override
	public boolean isResponse(IoSession session, Object message) {
		if (message instanceof DataPacket) {
			DataPacket hb = (DataPacket) message;
			if (hb.getType().equals(Constant.TYPE_HEARTBEAT)) {
				logger.info("Resoponse heartbeat received from: " + session.getRemoteAddress());
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getRequest(IoSession session) {
		DataPacket hb = new DataPacket();
		hb.setType(Constant.TYPE_HEARTBEAT);
		return hb;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		return null;// 服务器不会主动发心跳请求，直接返回null
	}
	
	@Override
	public boolean isRequest(IoSession session, Object message) {
		return false; // 服务器不会主动发心跳请求，直接返回false
	}

}
