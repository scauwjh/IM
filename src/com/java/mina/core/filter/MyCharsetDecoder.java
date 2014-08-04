package com.java.mina.core.filter;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.java.mina.constant.Constant;
import com.java.mina.core.model.Message;
import com.java.mina.core.model.User;
import com.java.mina.util.Debug;

public class MyCharsetDecoder extends CumulativeProtocolDecoder {

	private final Charset charset;
	
	public MyCharsetDecoder(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
		CharsetDecoder decoder = charset.newDecoder();
		String header = null, user = null, password = null, sender = null,
				receiver = null, length = null, message = null, timeStamp = null;
		int line = 0, count = 0;
		List<Message> msgList = new ArrayList<Message>();
		boolean ifList = false;
		while (in.hasRemaining()) {
			count ++;
			byte b = in.get();
			buffer.put(b);
			if (b == 10 && line == 0) {
				buffer.flip();
				header = buffer.getString(count, decoder);
				header = header.substring(0, header.length() - 1);
				line ++;
				count = 0;
				buffer.clear();
				Debug.println("received header: " + header);
			} else if (b == 10 && line > 0) {
				if (header.equals(Constant.LOGIN)) {
					buffer.flip();
					if (line  == 1) {
						user = buffer.getString(count, decoder);
						user = user.substring(0, user.length() - 1);
					} else if (line == 2) {
						password = buffer.getString(count, decoder);
						password = password.substring(0, password.length() - 1);
					} else if (line == 3) {
						timeStamp = buffer.getString(count, decoder);
						timeStamp = timeStamp.substring(0, timeStamp.length() - 1);
						User object = new User();
						object.setHeader(header);
						object.setUser(user);
						object.setPassword(password);
						object.setTimeStamp(timeStamp);
						out.write(object);
						break;
					}
					line ++;
					count = 0;
					buffer.clear();
				} else if (header.equals(Constant.SEND)) {
					if (line  == 1) {
						buffer.flip();
						sender = buffer.getString(count, decoder);
						sender = sender.substring(0, sender.length() - 1);
						line ++;
						count = 0;
						buffer.clear();
					} else if (line == 2) {
						buffer.flip();
						receiver = buffer.getString(count, decoder);
						receiver = receiver.substring(0, receiver.length() - 1);
						line ++;
						count = 0;
						buffer.clear();
					} else if (line == 3) {
						buffer.flip();
						timeStamp = buffer.getString(count, decoder);
						timeStamp = timeStamp.substring(0, timeStamp.length() - 1);
						line ++;
						count = 0;
						buffer.clear();
					} else if (line == 4) {
						buffer.flip();
						length = buffer.getString(count, decoder);
						length = length.substring(0, length.length() - 1);
						line ++;
						count = 0;
						buffer.clear();
					} else if (line >= 5 && count >= Integer.valueOf(length)) {
						buffer.flip();
						message = buffer.getString(count, decoder);
						message = message.substring(0, message.length() - 1);
						Message msg = new Message();
						msg.setHeader(header);
						msg.setSender(sender);
						msg.setReceiver(receiver);
						msg.setMessage(message);
						msg.setTimeStamp(timeStamp);
						buffer.clear();
						if (in.hasRemaining()) {
							msgList.add(msg);
							ifList = true;
							count = 0;
							line = 0;
							continue;
						} else if (ifList) {
							out.write(msgList);
						} else if (!ifList) {
							out.write(msg);
						}
						break;
					}
				} else if (header.equals("heartbeat")) {
					out.write("");
					break;
				}
			} // end else if
		} // end while
		return false;
	}


}
