package com.java.mina.constant;

public class Constant {
	
	public static final String ACCOUNT = "account";
	
	public static final String SESSION_ACCOUNT = "session_account";
	
	public static final String TYPE_LOGIN = "loginType";
	
	public static final String TYPE_SEND = "sendType";
	
	public static final String TYPE_HEARTBEAT = "heartbeatType";
	
	public static final String CONTENT_TYPE_HEARTBEAT = "contentTypeHeartbeat";
	
	public static final String CONTENT_TYPE_MESSAGE = "contentTypeMessage";
	
	public static final String CONTENT_TYPE_IMAGE = "contentTypeImage";
	
	public static final String CONTENT_TYPE_LOGIN = "contentTypeLogin";
	
	public static final String DATA_LENGTH = "dataLength";
		
	public static final String CHARSET = "UTF-8";
	
	
	public final static Integer SERVER_HEARTBEAT_INTERVAL = 60;
	
	public final static Integer CLIENT_HEARTBEAT_INTERVAL = 40;
	
	public final static Integer HEARTBEAT_TIMEOUT = 30;
	
	public final static String HEARTBEAT_REQUEST = "0x11";
	
	public final static String HEARTBEAT_RESPONSE = "0x12";
	
	
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
	 * send message overtime: 10s
	 */
	public static final Long MESSAGE_OVERTIME = 10000L;
	/**
	 * send image overtime: 30s
	 */
	public static final Long IMAGE_OVERTIME = 30000L;
	
	
	
	public static String SERVER_HOST = "127.0.0.1";
	
	public static Integer TEXT_PORT = 7777;
	
	public static Integer IMAGE_PORT = 8888;
	
	public static Integer SERVER_BUFFER_SIZE = 4096;
	
	public static Integer SERVER_CACHE_SIZE = 100000;
}
