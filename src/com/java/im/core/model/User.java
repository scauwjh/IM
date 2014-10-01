package com.java.im.core.model;

import org.apache.mina.core.session.IoSession;

import com.java.im.constant.Constant;

public class User {

	private String userId;

	private IoSession textSession;

	private IoSession mediaSession;

	public User() {

	}

	public User(String userId) {
		this.userId = userId;
	}

	public IoSession getIoSession(Integer port) {
		if (port.equals(Constant.TEXT_PORT)) {
			return this.textSession;
		} else if (port.equals(Constant.IMAGE_PORT)) {
			return this.mediaSession;
		} else {
			return null;
		}
	}

	public Boolean setIoSession(Integer port, IoSession session) {
		if (port.equals(Constant.TEXT_PORT)) {
			if (this.textSession == null && session == null)
				return false;
			this.textSession = session;
		} else if (port.equals(Constant.IMAGE_PORT)) {
			if (this.mediaSession == null && session == null)
				return false;
			this.mediaSession = session;
		} else {
			return false;
		}
		return true;
	}

	/**
	 * If the user have sessions
	 * 
	 * @return false is sessions is all null, or to say is logout
	 */
	public Boolean ifLogin() {
		return !((textSession == null) && (mediaSession == null));
	}

	public void logout(boolean immediately) {
		this.textSession.close(immediately);
		this.mediaSession.close(immediately);
	}

	public IoSession getTextSession() {
		return textSession;
	}

	public void setTextSession(IoSession textSession) {
		this.textSession = textSession;
	}

	public IoSession getMediaSession() {
		return mediaSession;
	}

	public void setMediaSession(IoSession mediaSession) {
		this.mediaSession = mediaSession;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
