package com.java.mina.demo;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.java.mina.constant.Constant;
import com.java.mina.core.server.Server;
import com.java.mina.util.Debug;

public class IMLoader implements Servlet {

	private static Server server;
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// start im server
		Debug.println("IM server is starting");
		server = new Server(Constant.SERVER_BUFFER_SIZE);
		server.runServer();
		Debug.println("IM server finish starting");
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
