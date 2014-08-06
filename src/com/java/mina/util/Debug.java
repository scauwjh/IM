package com.java.mina.util;

public class Debug {
	
	private final static Boolean IS_DEBUG = false;
	
	public static void println(Object value) {
		if (IS_DEBUG)
			System.out.println(value);
	}
	
	public static void print(Object value) {
		if (IS_DEBUG)
			System.out.print(value);
	}
	
	public static void printStackTrace(Object e) {
		Exception exception = (Exception) e;
		if (IS_DEBUG)
			exception.printStackTrace();
	}
}
