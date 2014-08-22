package com.java.mina.core.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.core.model.BytePacket;
import com.java.mina.util.Debug;

public class MyCharsetDecoder extends CumulativeProtocolDecoder {

	public final static Logger logger = LoggerFactory.getLogger(MyCharsetDecoder.class);
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		BytePacket encode = new BytePacket();
		byte[] header = null;
		byte[] body = null;
		Integer length = null;
		// mark for reset
		in.mark();
		try {
			// get header
			length = in.getInt();
			if (length > in.remaining()) {
				logger.info("Data may not enough in decoder");
				in.reset();
				return false;
			}
			header = new byte[length];
			in.get(header, 0, length);
			encode.setHeader(header);
			// get body
			length = in.getInt();
			if (length > 0 && length > in.remaining()) {
				logger.info("Data may not enough in decoder");
				in.reset();
				return false;
			}
			if (length > 0) {
				body = new byte[length];
				in.get(body, 0, length);
				encode.setBody(body);
			}
			// write object
			out.write(encode.toDataPacket());
			// if remaining
			if (in.remaining() > 0) {
				Debug.println("Buffer is remaining");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.info("Exception: Data may not enough in decoder");
			Debug.printStackTrace(e);
			in.reset();
			return false;
		}
	}

}
