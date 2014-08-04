package com.java.mina.core.filter;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyCharsetCodecFactory implements ProtocolCodecFactory {

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new MyCharsetEncoder(Charset.forName("UTF-8"));
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new MyCharsetDecoder(Charset.forName("UTF-8"));
	}

}
