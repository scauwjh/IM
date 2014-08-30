package com.java.im.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.java.im.constant.Constant;

public class PropertiesUtil {
	
	public static Properties properties(String path) throws Exception {
		InputStream in = null;
		Properties properties = null;
		try {
			in = new FileInputStream(path);
			InputStreamReader read = new InputStreamReader(in, Constant.CHARSET);
			properties = new Properties();
			properties.load(read);
			in.close();
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: 异常出错，读取失败");
			throw e;
		} finally {
			in = null;
		}
	}
	
	public static Map<String, String> getProperties(String path) {
		try {
			Properties properties = PropertiesUtil.properties(path);
			Enumeration<?> em = properties.propertyNames();
			Map<String, String> map = new HashMap<String, String>();
			while (em.hasMoreElements()) {
				String key = (String) em.nextElement();
				String value = properties.get(key).toString();
//				System.out.println(key + ":" + value);
				map.put(key, value);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		PropertiesUtil.getProperties("C:\\Users\\asus\\Desktop\\test.properties");
	}
	
}
