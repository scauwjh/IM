package com.java.im.demo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class EhCacheDemo {
	
	public static void main(String[] args) {
		CacheManager manager = CacheManager.create();
		Cache cache = new Cache(new CacheConfiguration("test", 10000)
				.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
				.eternal(false)
				.timeToLiveSeconds(60)
				.timeToIdleSeconds(30)
				.diskExpiryThreadIntervalSeconds(0)
				.persistence(
						new PersistenceConfiguration()
								.strategy(Strategy.LOCALTEMPSWAP)));
		manager.addCache(cache);
		
		Element e = new Element("tmp", "123");
		cache.put(e);
		Element tmp = cache.get("tmp");
		System.out.println(tmp.getObjectValue());
		manager.shutdown();
	}
}
