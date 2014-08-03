package com.java.mina.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	
	private final static ObjectMapper mapper = new ObjectMapper();
	
	public static String toJson(Object object) {
		
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object toObject(String json, Class<?> c) {
		try {
			return mapper.readValue(json, c);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
