package com.java.mina.core.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.java.mina.constant.Constant;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.util.Debug;

public class MyCharsetDecoder extends CumulativeProtocolDecoder {

	private Charset charset;
	
	private CharsetDecoder decoder;
	
	private IoBuffer buffer;
	
	private Integer line;
	
	private Integer count;
	
	public MyCharsetDecoder(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		buffer = IoBuffer.allocate(10024).setAutoExpand(true);
		decoder = charset.newDecoder();
		line = 0;
		count = 0;
		in.mark();
		String header = null, user = null, password = null, sender = null,
				receiver = null, message = null, timeStamp = null;
		int length = 0;
		try {
			while (in.hasRemaining()) {
				count ++;
				byte b = in.get();
				buffer.put(b);
				if (b == 10 && line == 0) {
					header = getString();
					Debug.println("received header: " + header);
				} else if (b == 10 && line > 0) {
					if (header.equals(Constant.LOGIN)) {
						if (line  == 1) {
							user = getString();
						} else if (line == 2) {
							password = getString();
						} else if (line == 3) {
							timeStamp = getString();
							User object = new User();
							object.setHeader(header);
							object.setUser(user);
							object.setPassword(password);
							object.setTimeStamp(timeStamp);
							out.write(object);
							break;
						}
					} else if (header.equals(Constant.SEND)) {
						if (line  == 1) {
							sender = getString();
						} else if (line == 2) {
							receiver = getString();
						} else if (line == 3) {
							timeStamp = getString();
							length = in.getInt();
						} else if (line >= 4 && count >= length) {
							message = getString();
							Message msg = new Message();
							msg.setHeader(header);
							msg.setSender(sender);
							msg.setReceiver(receiver);
							msg.setMessage(message);
							msg.setTimeStamp(timeStamp);
							out.write(msg);
							break;
						}
					} else if (header.equals(Constant.IMAGE)) {
						if (line == 1) {
							sender = getString();
						} else if (line == 2) {
							receiver = getString();
						} else if (line == 3) {
							timeStamp = getString();
							length = in.getInt();
							int remaining = in.remaining();
							Debug.println("image byte length: " + length + " remaining: " + remaining);
							if (remaining < length) {
								in.reset();
								return false;
							}
							byte[] dst = new byte[length + 1];
							in.get(dst, 0, length);
							Image img = new Image();
							img.setHeader(header);
							img.setSender(sender);
							img.setReceiver(receiver);
							img.setImage(dst);
							img.setTimeStamp(timeStamp);
							out.write(img);
							break;
						}
					} else if (header.equals(Constant.HEARTBEAT)) {
						out.write("");
						break;
					} else if (header.equals(Constant.STRING)){
						out.write(getString());
					}
				} // end else if
			} // end while
		} catch (Exception e) {
			in.reset();
			return false;
		}
		// no exception throw
		if (in.remaining() > 0)
			return true;
		else return false;
	}

	
	protected String getString() throws IOException {
		buffer.flip();
		String str = buffer.getString(count, decoder);
		str = str.substring(0, str.length() - 1);
		buffer.clear();
		line ++;
		count = 0;
		return str;
	}

}
