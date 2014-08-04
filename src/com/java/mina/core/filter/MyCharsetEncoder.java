package com.java.mina.core.filter;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;

public class MyCharsetEncoder extends ProtocolEncoderAdapter {

	private final Charset charset;
	
	public MyCharsetEncoder(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		CharsetEncoder encoder = charset.newEncoder();
		IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
		if (message instanceof User) {
			// login packet
			User user = (User) message;
			buffer.putString(user.getHeader() + "\n", encoder);
			buffer.putString(user.getUser() + "\n", encoder);
			buffer.putString(user.getPassword() + "\n", encoder);
			buffer.putString(user.getTimeStamp() + "\n", encoder);
		} else if (message instanceof Message) {
			Message msg = (Message) message;
			buffer.putString(msg.getHeader() + "\n", encoder);
			buffer.putString(msg.getSender() + "\n", encoder);
			buffer.putString(msg.getReceiver() + "\n", encoder);
			buffer.putString(msg.getTimeStamp() + "\n", encoder);
			buffer.putString(msg.getMessage().getBytes(charset).length + "\n", encoder);
			buffer.putString(msg.getMessage() + "\n", encoder);
		}
		buffer.flip();
		out.write(buffer);
	}
}
