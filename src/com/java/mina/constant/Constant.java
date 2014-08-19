package com.java.mina.constant;

public class Constant {
	
	public static final String ACCOUNT = "account";
	
	public static final String SESSION_ACCOUNT = "session_account";
	
	public static final String LOGIN = "login";
	
	public static final String HEARTBEAT = "heartbeat";
	
	public static final String MESSAGE = "message";
	
	public static final String IMAGE = "image";
	
	public static final String DATA_LENGTH = "dataLength";
	
	public static final String STRING = "string";
	
	public static final String CHARSET = "UTF-8";
	
	/**
	 * connect overtime: 10s
	 */
	public static final Long CONNECT_OVERTIME = 10000L;
	/**
	 * session overtime： 5min
	 */
	public static final Long SESSION_OVERTIME = 300000L;
	/**
	 * login overtime: 10s
	 */
	public static final Long LOGIN_OVERTIME = 10000L;
	/**
	 * heart beat overtime: 10s
	 */
	public static final Long HEARTBEAT_OVERTIME = 10000L;
	/**
	 * send message overtime: 10s
	 */
	public static final Long MESSAGE_OVERTIME = 10000L;
	/**
	 * send image overtime: 30s
	 */
	public static final Long IMAGE_OVERTIME = 30000L;
	
	
	
	public static String SERVER_HOST = "127.0.0.1";
	
	public static Integer TEXT_PORT = 9999;
	
	public static Integer IMAGE_PORT = 8888;
	
	public static Integer HEARTBEAT_PORT = 7777;
	
	public static Integer SERVER_BUFFER_SIZE = 4096;
	
	public static Integer SERVER_CACHE_SIZE = 100000;
}
