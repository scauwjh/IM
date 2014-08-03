package com.java.mina.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.mina.util.Debug;
import com.java.mina.util.lrucache.LRUCache;
import com.java.mina.util.lrucache.LRUEntry;

public class OfflineMessage {
	
	private final static Logger logger = LoggerFactory.getLogger(OfflineMessage.class);
	
	public static LRUCache<String, List<String>> messageQueue = new LRUCache<String, List<String>>(100000) {
		@Override
		protected void remove(LRUEntry<String, List<String>> node) {
			logger.info("cache is full, remove and save the oldest message to DB");
			Debug.println("cache is full...");
			// to save the message to DB
			// ...
			// ...
		}
	};
	
}