package com.java.mina.constant;

public class Constant {
	
	
	public static final Long ONE_MINUTE_MILLIONSECOND = 60000L;
	/**
	 * session overtime： 5min
	 */
	public static final Long SESSION_OVERTIME = 30000L;//300000L;
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
	
	public static final String ACCOUNT = "account";
	
	public static final String SESSION_ACCOUNT = "session_account";
	
	public static final String LOGIN = "login";
	
	public static final String HEARTBEAT = "heartbeat";
	
	public static final String SEND = "send";
	
	public static final String IMAGE = "image";
	
	public static final String DATA_LENGTH = "dataLength";
	
	public static final String STRING = "string";
	
	public final static String REMOTE_ADDRESS = "114.215.176.122";//"127.0.0.1";//"124.173.68.84";//
	
	public static final Integer TEXT_PORT = 9999;
	
	public static final Integer IMAGE_PORT = 8888;
	
	public static final Integer HEARTBEAT_PORT = 7777;
	
	public final static String CHARSET = "UTF-8";
	
	public final static Long CONNECT_TIMEOUT = 3000L;
	
	public final static Integer CACHE_SIZE = 100000;
	
	public final static Integer SERVER_BUFFER_SIZE = 4096;
}
