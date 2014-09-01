package com.java.im.core.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.java.im.constant.Constant;
import com.java.im.core.model.BytePacket;
import com.java.im.core.model.DataPacket;

public class GlobalCharsetEncoder extends ProtocolEncoderAdapter {
	
	/**
	 * <p>header:</p> 
	 * <p>type:...\n</p>
	 * <p>sender:...\n</p>
	 * <p>receiver:...\n</p>
	 * <p>content-length:...\n</p>
	 * <p>\n</p>
	 * <p>body:</p>
	 * <p>......</p>
	 */
	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
		DataPacket packet = (DataPacket) message;
		if (packet.getType().equals(Constant.TYPE_HEARTBEAT)) {
			buffer.putInt(-1);
		} else {
			BytePacket content = packet.toBytePacket();
			buffer.putInt(content.getHeader().length);
			buffer.put(content.getHeader());
			if (content.getBody() != null) {
				buffer.putInt(content.getBody().length);
				buffer.put(content.getBody());
			} else {
				buffer.putInt(0);
			}
		}
		buffer.flip();
		out.write(buffer);
	}
}
