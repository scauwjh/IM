package com.java.im.core.service;

import java.util.List;

import com.java.im.util.lrucache.LRUEntry;

public class OfflineMessage {

	public void saveOfflineMessage(LRUEntry<String, List<Object>> node) {
		// to save the node(save the list of the node) to DB
		// list object would be Message or Image
//		System.out.println("save!!!!");
	}
	
	/**
	 * 是否需要先将溢出的消息再用一条队列来缓存住，
	 * 到达一定数量再一次性写入数据库呢？
	 * @return
	 */
	public List<Object> getOfflineMessage() {
		// to get the list from DB and return
		// list object would be Message or Image
//		System.out.println("get!!!!");
		return null;
	}
	
}
