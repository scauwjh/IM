package com.java.im.demo;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

public class EhCacheDemo {
	
	public static void main(String[] args) {
		URL url = EhCacheDemo.class.getResource("/ehcache.xml");
		CacheManager manager = CacheManager.newInstance(url);//CacheManager.create();
		Cache cache = new Cache(new CacheConfiguration("test", 10000));
		manager.addCache(cache);
		
		Element e = new Element("tmp", "123");
		cache.put(e);
		Element tmp = cache.get("tmp");
		System.out.println(tmp.getObjectValue());
		manager.shutdown();
	}
}
