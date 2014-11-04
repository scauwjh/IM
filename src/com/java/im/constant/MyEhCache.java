package com.java.im.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

public class MyEhCache {

	public static final Logger logger = LoggerFactory
			.getLogger(MyEhCache.class);

	private static CacheManager cacheManager;

	public synchronized static CacheManager getInstance() {
		try {
			if (cacheManager == null) {
				cacheManager = CacheManager.newInstance(Thread.currentThread()
						.getContextClassLoader().getResource("ehcache.xml"));
				Cache messageCache = new Cache(new CacheConfiguration(
						Constant.MESSAGE_QUEUE, Constant.SERVER_CACHE_SIZE));
				cacheManager.addCache(messageCache);
				logger.info("Read properties from ehcache.xml");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return cacheManager;
	}
}
