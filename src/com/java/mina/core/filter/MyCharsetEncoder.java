package com.java.mina.core.filter;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.java.mina.constant.Constant;
import com.java.mina.core.model.Heartbeat;
import com.java.mina.core.model.Image;
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
			// send message
			Message msg = (Message) message;
			buffer.putString(msg.getHeader() + "\n", encoder);
			buffer.putString(msg.getSender() + "\n", encoder);
			buffer.putString(msg.getReceiver() + "\n", encoder);
			buffer.putString(msg.getTimeStamp() + "\n", encoder);
			buffer.putInt(msg.getMessage().getBytes().length);
			buffer.putString(msg.getMessage() + "\n", encoder);
		} else if (message instanceof Image) {
			// send image
			Image image = (Image) message;
			buffer.putString(image.getHeader() + "\n", encoder);
			buffer.putString(image.getSender() + "\n", encoder);
			buffer.putString(image.getReceiver() + "\n", encoder);
			buffer.putString(image.getTimeStamp() + "\n", encoder);
			buffer.putInt(image.getImage().length);
			buffer.put(image.getImage(), 0, image.getImage().length);
		} else if (message instanceof Heartbeat) {
			Heartbeat hb = (Heartbeat) message;
			buffer.putString(hb.getHeader() + "\n", encoder);
			buffer.putString(hb.getAccount() + "\n", encoder);
			buffer.putString(hb.getTimeStamp() + "\n", encoder);
		} else if (message instanceof String) {
			buffer.putString(Constant.STRING + "\n", encoder);
			buffer.putString(message + "\n", encoder);
		}
		buffer.flip();
		out.write(buffer);
	}
}
