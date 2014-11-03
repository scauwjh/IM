package com.java.im.constant;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

public class MyEhCache {
	
	private static CacheManager cacheManager;
	
	public synchronized static CacheManager getInstance() {
		if (cacheManager == null) {
			cacheManager = CacheManager.newInstance(
					MyEhCache.class.getResource("/ehcache.xml")
				);
			Cache messageCache = new Cache(
					new CacheConfiguration(Constant.MESSAGE_QUEUE, Constant.SERVER_CACHE_SIZE)
			);
			cacheManager.addCache(messageCache);
		}
		return cacheManager;
	}
}
