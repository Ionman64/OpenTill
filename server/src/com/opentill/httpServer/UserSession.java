package com.opentill.httpServer;

import com.opentill.main.Utils;
import com.opentill.models.UserModel;

public class UserSession {
	private String id = new String();
	public SessionState state = SessionState.UNKNOWN;
	public Long expiryTimeStamp = -1L;
	private boolean expires = true;
	private Long DEFAULTEXPIRYTIME = 3600L; // One Hour
	private UserModel user = new UserModel();

	public UserSession(String id, UserModel user) {
		this.id = id;
		this.expiryTimeStamp = Utils.getCurrentTimeStamp() + DEFAULTEXPIRYTIME;
		this.expires = false;
		this.state = SessionState.ACTIVE;
		this.user = user;
	}

	public void setExpires(boolean expires) {
		this.expires = expires;
	}

	public String getId() {
		return this.id;
	}

	public UserSession(String id, UserModel user, Long expiryTimeStamp) {
		this.id = id;
		this.expiryTimeStamp = expiryTimeStamp;
		if (expiryTimeStamp == -1L) {
			this.expires = false;
		}
		this.state = SessionState.ACTIVE;
		this.user = user;
	}

	public boolean updateSession() {
		if ((this.expires) && (this.expiryTimeStamp < Utils.getCurrentTimeStamp())) {
			this.state = SessionState.EXPIRED;
			return false;
		}
		return true;
	}

	public UserModel getValue() {
		this.updateSession();
		if (this.state == SessionState.EXPIRED) {
			System.out.println("Warning: Expired Session Being Read");
		}
		return this.user;
	}
}
