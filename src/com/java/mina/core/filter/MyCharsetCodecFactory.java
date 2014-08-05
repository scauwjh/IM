package com.java.mina.core.filter;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyCharsetCodecFactory implements ProtocolCodecFactory {

	private Charset charset;
	
	public MyCharsetCodecFactory(String charset) {
		this.charset = Charset.forName(charset);
	}
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new MyCharsetEncoder(charset);
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new MyCharsetDecoder(charset);
	}

}
