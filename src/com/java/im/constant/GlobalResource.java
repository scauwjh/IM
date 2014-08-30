package com.java.im.constant;

import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.core.service.OfflineMessage;
import com.java.im.util.lrucache.LRUCache;
import com.java.im.util.lrucache.LRUEntry;

public class GlobalResource {
	
	private final static Logger logger = LoggerFactory.getLogger(GlobalResource.class);
	
	public static LRUCache<String, List<Object>> messageQueue = new LRUCache<String, List<Object>>(Constant.SERVER_CACHE_SIZE) {
		@Override
		protected boolean ifRemove() {
			return true; // true to remove the node when get method is called
		}
		
		@Override
		protected void remove(LRUEntry<String, List<Object>> node) {
			logger.info("cache is full, remove and save the oldest message to DB");
			// cache is full, to save the node to DB
			new OfflineMessage().saveOfflineMessage(node);
		}
	};
	
	public static HashMap<String, IoSession> sessionMap = new HashMap<String, IoSession>();
	
	public static HashMap<String, Object> userMap = new HashMap<String, Object>();
	
	public static Integer sessionCount = 0;
	
	public synchronized static Integer getSessionCount(Integer add) {
		sessionCount += add;
		return sessionCount;
	}
	
}
