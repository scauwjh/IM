package com.java.im.constant;

import java.util.HashMap;

import net.sf.ehcache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.java.im.core.model.User;

public class GlobalResource {
	
	public final static Logger logger = LoggerFactory.getLogger(GlobalResource.class);
	
	public static Cache messageCache = MyEhCache.getInstance().getCache(Constant.MESSAGE_QUEUE);
	
	public static HashMap<String, User> userMap = new HashMap<String, User>();
	
}
