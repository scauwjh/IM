package com.java.im.constant;

public class Constant {
	
	public static final String ACCOUNT = "account";
	
	public static final String SESSION_ACCOUNT = "session_account";
	
	public static final String TYPE_LOGIN = "loginType";
	
	public static final String TYPE_SEND = "sendType";
	
	public static final String TYPE_HEARTBEAT = "heartbeatType";

	public static final String TYPE_RETURN = "returnType";
	
	public static final String SESSION_PORT = "sessionPort";
	
	public static final String CONTENT_TYPE_HEARTBEAT = "contentTypeHeartbeat";
	
	public static final String CONTENT_TYPE_MESSAGE = "contentTypeMessage";
	
	public static final String CONTENT_TYPE_IMAGE = "contentTypeImage";
	
	public static final String CONTENT_TYPE_LOGIN = "contentTypeLogin";
	
	public static final String DATA_LENGTH = "dataLength";
	
	public static final String CHARSET = "UTF-8";
	
	public static final String SERVER_NAME = "#UFRIEND_SERVER#";
	
	public static final Integer DEBUG_ERROR = 0;
	
	public static final Integer DEBUG_WARN = 1;
	
	public static final Integer DEBUG_INFO = 2;
	
	public static final Integer DEBUG_DEBUG = 3;
	
	/**
	 * server heart beat timeout 240s
	 */
	public final static Integer SERVER_HEARTBEAT_INTERVAL = 240;
	/**
	 * client heart beat interval 180s
	 */
	public final static Long CLIENT_HEARTBEAT_INTERVAL = 180000L;
	/**
	 * heart beat time out 3s
	 */
	public final static Long HEARTBEAT_TIMEOUT = 3000L;
	
	/**
	 * connect overtime: 20s
	 */
	public static final Long CONNECT_OVERTIME = 20000L;
	/**
	 * session overtime： 5min
	 */
	public static final Long SESSION_OVERTIME = 300000L;
	/**
	 * login overtime: 20s
	 */
	public static final Long LOGIN_OVERTIME = 20000L;
	/**
	 * send message overtime: 5s
	 */
	public static final Long MESSAGE_OVERTIME = 5000L;
	/**
	 * send image overtime: 10s
	 */
	public static final Long IMAGE_OVERTIME = 10000L;
	
	/**
	 * default host 127.0.0.1
	 */
	public static String SERVER_HOST = "127.0.0.1";
	/**
	 * default text port 7777
	 */
	public static Integer TEXT_PORT = 7777;
	/**
	 * default image port 8888
	 */
	public static Integer IMAGE_PORT = 8888;
	/**
	 * default server buffer size 4096
	 */
	public static Integer SERVER_BUFFER_SIZE = 4096;
	/**
	 * default server cache size 100000
	 */
	public static Integer SERVER_CACHE_SIZE = 100000;
	/**
	 * default isDebug true
	 */
	public static Boolean IS_DEBUG = false;
}
