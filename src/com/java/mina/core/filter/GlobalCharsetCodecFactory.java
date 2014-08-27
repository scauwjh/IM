package com.java.mina.core.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class GlobalCharsetCodecFactory implements ProtocolCodecFactory {
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new GlobalCharsetEncoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new GlobalCharsetDecoder();
	}

}
