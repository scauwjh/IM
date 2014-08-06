package com.java.mina.constant;

import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.util.Debug;
import com.java.mina.util.lrucache.LRUCache;
import com.java.mina.util.lrucache.LRUEntry;

public class GlobalResource {
	
	private final static Logger logger = LoggerFactory.getLogger(GlobalResource.class);
	
	public static LRUCache<String, List<Object>> messageQueue = new LRUCache<String, List<Object>>(100000) {
		@Override
		protected boolean ifRemove() {
			return true; // true to remove the node when get method is called
		}
		
		@Override
		protected void remove(LRUEntry<String, List<Object>> node) {
			logger.info("cache is full, remove and save the oldest message to DB");
			Debug.println("cache is full...");
			// to save the message to DB
			// ...
			// ...
		}
	};
	
	public static HashMap<String, IoSession> sessionMap = new HashMap<String, IoSession>();
	
}
