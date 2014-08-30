package com.java.mina.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.constant.Constant;

public class Debug {
	
	public static final Logger logger = LoggerFactory.getLogger(Debug.class);
	
	public static void println(Object value) {
		if (Constant.IS_DEBUG) {
			System.out.println("---------- " + value + " ----------");
		} else {
			logger.debug(value + "");
		}
			
	}
	
	public static void printStackTrace(Object e) {
		Exception exception = (Exception) e;
		if (Constant.IS_DEBUG) {
			exception.printStackTrace();
		}
	}
}
