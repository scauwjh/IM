package com.java.mina.util;

import java.util.HashMap;
import java.util.Map;

/** 
 * @author wjh E-mail: 472174314@qq.com
 * @version 创建时间：2014年4月1日 下午1:38:55 
 * 
 *
 */
public class StringUtil {
	
	public static String randString(Integer length) {
		String base = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			Integer rand = (int) (Math.random() * base.length());
			sb.append(base.charAt(rand));
		}
		return sb.toString();
	}
	
	/**
	 * @param url
	 * @param type 0:url has no '?'  1:url has '?'
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getUrlParameters(String url, Integer type) throws Exception {
		if (url.contains("?")) {
			if (type.equals(0)) {
				Exception e = new Exception("for tye 0 but url has '?', try type 1");
				throw e;
			}
			String[] part = url.split("[?]");
			if (part.length > 2) {
				Exception e = new Exception("url has to many '?'");
				throw e;
			}
			url = part[part.length - 1];
		} else if(type.equals(1)) {
			Exception e = new Exception("for tye 1 but url has no '?', try type 0");
			throw e;
		}
		String[] params = url.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < params.length; i++) {
			String[] tmp = params[i].split("=");
			if (tmp.length != 2){
				Exception e = new Exception("url is illegal");
				throw e;
			}
			map.put(tmp[0], tmp[1]);
		}
		return map;
	}
	
	public static String returnMessage(Integer code, String msg) {
		return "{\"ret\":\"" + code + "\", \"msg\":\"" + msg + "\"}";
	}
	
	public static void main(String[] args) {
		System.out.println(returnMessage(-1, "test"));
		System.out.println(randString(32));
		//String url1 = "username=user&password=pass";
		String url2 = "http://www.baidu.com?username=user&password=pass";
		try {
			Map<String, String> map = getUrlParameters(url2, 1);
			System.out.println(map.get("password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
