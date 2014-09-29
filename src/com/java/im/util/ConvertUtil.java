package com.java.im.util;

public class ConvertUtil {
	
	/**
	 * int to byte[]
	 * @param i
	 * @return byte[]
	 */
	public static byte[] i2b(int i) {
		return new byte[] { (byte) ((i >> 24) & 0xFF),
				(byte) ((i >> 16) & 0xFF), (byte) ((i >> 8) & 0xFF),
				(byte) (i & 0xFF) };
	}

	/**
	 * byte[] to int (4 byte)
	 * @param b
	 * @return int
	 */
	public static int b2i(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}
}
