package com.java.mina.constant;

public class Constant {
	
	
	public static final Long ONE_MINUTE_MILLIONSECOND = 60000L;
	/**
	 * session overtime： 5min
	 */
	public static final Integer SESSION_OVERTIME = 300000;
	/**
	 * login overtime: 10s
	 */
	public static final Long LOGIN_OVERTIME = 10000L;
	/**
	 * heart beat overtime: 3s
	 */
	public static final Long HEARTBEAT_OVERTIME = 3000L;
	
	public static final String ACCOUNT = "account";
	
	public static final String SESSION_ACCOUNT = "session_account";
	
	public static final String LOGIN = "login";
	
	public static final String HEARTBEAT = "heartbeat";
	
	public static final String SEND = "send";
	
	public static final String IMAGE = "image";
	
	public static final String DATA_LENGTH = "dataLength";
	
	public static final String STRING = "string";
	
	public final static String REMOTE_ADDRESS = "127.0.0.1";
	
	public static final Integer TEXT_PORT = 9999;
	
	public static final Integer IMAGE_PORT = 8888;
	
	public final static String CHARSET = "UTF-8";
	
	public final static Long CONNECT_TIMEOUT = 3000L;
	
	public final static Integer CACHE_SIZE = 100000;
}
