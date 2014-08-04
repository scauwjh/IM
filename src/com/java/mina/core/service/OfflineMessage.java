package com.java.mina.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.core.model.Message;
import com.java.mina.util.Debug;
import com.java.mina.util.lrucache.LRUCache;
import com.java.mina.util.lrucache.LRUEntry;

public class OfflineMessage {
	
	private final static Logger logger = LoggerFactory.getLogger(OfflineMessage.class);
	
	public static LRUCache<String, List<Message>> messageQueue = new LRUCache<String, List<Message>>(100000) {
		@Override
		protected boolean ifRemove() {
			return true; // true to remove the node when get method is called
		}
		
		@Override
		protected void remove(LRUEntry<String, List<Message>> node) {
			logger.info("cache is full, remove and save the oldest message to DB");
			Debug.println("cache is full...");
			// to save the message to DB
			// ...
			// ...
		}
	};
	
}
