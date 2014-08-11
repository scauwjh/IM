package com.java.mina.core.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;
import com.java.mina.core.model.Heartbeat;
import com.java.mina.core.model.Image;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.util.Debug;

public class MyCharsetDecoder extends CumulativeProtocolDecoder {

	public final static Logger logger = LoggerFactory.getLogger(MyCharsetDecoder.class);
	
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
							Integer status = in.getInt();
							User object = new User();
							object.setHeader(header);
							object.setUser(user);
							object.setPassword(password);
							object.setTimeStamp(timeStamp);
							object.setStatus(status);
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
							Integer type = in.getInt();
							length = in.getInt();
							if (length > in.remaining()) {
								in.reset();
								return false;
							}
							message = in.getString(length, decoder);
							Message msg = new Message();
							msg.setHeader(header);
							msg.setSender(sender);
							msg.setReceiver(receiver);
							msg.setType(type);
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
							if (length > in.remaining()) {
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
						if (line == 1) {
							user = getString();
						}
						else if (line == 2) {
							timeStamp = getString();
							Heartbeat hb = new Heartbeat();
							hb.setHeader(header);
							hb.setAccount(user);
							hb.setTimeStamp(timeStamp);
							out.write(hb);
							break;
						}
					} else if (header.equals(Constant.STRING)){
						String tmp = getString();
						System.out.println(tmp);
						out.write(tmp);
						break;
					}
				} // end else if
			} // end while
		} catch (Exception e) {
			logger.info("data may not enough in decoder");
			in.reset();
			return false;
		}
		// no exception throw
		if (in.remaining() > 0) {
			Debug.println("buffer is remaining");
			return true;
		}
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
