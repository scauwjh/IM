package com.java.mina.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CharsetCodecFactory implements ProtocolCodecFactory {

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new CharsetEncoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new CharsetDecoder();
	}

}
