package com.java.im.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.constant.Constant;

public class Debug {
	
	public static final Logger logger = LoggerFactory.getLogger(Debug.class);
	
	private static final String[] debug = {"ERROR", "WARN", "INFO", "DEBUG"};
	
	/**
	 * 调试输出
	 * @param level, 0 is error, 1 is warn, 2 is info, 3 is debug
	 * @param value
	 */
	public static void println(Integer level, Object value) {
		if (Constant.IS_DEBUG) {
			System.out.println("------" + debug[level] + ": " + value);
		} else if(level < 3) {
			System.out.println("------" + debug[level] + ": " + value);
		}
			
	}
	
	public static void printStackTrace(Integer level, Object e) {
		Exception exception = (Exception) e;
		if (Constant.IS_DEBUG) {
			exception.printStackTrace();
		}
	}
}
