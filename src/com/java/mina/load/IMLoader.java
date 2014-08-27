package com.java.mina.load;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.java.mina.constant.Constant;
import com.java.mina.core.server.Server;
import com.java.mina.util.Debug;
import com.java.mina.util.PropertiesUtil;

public class IMLoader implements Servlet {

	private static Server server;
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// start im server
		Debug.println("Loading properties");
		loadProperties();
		Debug.println("IM server is starting");
		server = new Server();
		server.runServer();
		Debug.println("IM server finish starting");
	}

	/**
	 * 获取配置
	 */
	private void loadProperties() {
		String path = getClass().getClassLoader().getResource("/").getPath()
				+ "imconfigure.properties";
		Map<String, String> map = PropertiesUtil.getProperties(path);
		Constant.SERVER_HOST = map.get("serverHost");
		Constant.TEXT_PORT = Integer.valueOf(map.get("textPort"));
		Constant.IMAGE_PORT = Integer.valueOf(map.get("imagePort"));
		Constant.SERVER_BUFFER_SIZE = Integer.valueOf(map.get("bufferSize"));
		Constant.SERVER_CACHE_SIZE = Integer.valueOf(map.get("cacheSize"));
	}
	
	
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		
	}
	
	
	@Override
	public void destroy() {
		server.stopServer();
		Debug.println("IM server is stop");
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

}
