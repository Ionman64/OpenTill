package com.opentill.httpServer;

import com.opentill.main.Utils;

public class Session {
	private String id = new String();
	public SessionState state = SessionState.UNKNOWN;
	public Long expiryTimeStamp = -1L;
	private boolean expires = true;
	private Long DEFAULTEXPIRYTIME = 3600L; //One Hour
	private String value = new String();
	public Session(String id, String value) {
		this.id = id;
		this.expiryTimeStamp = Utils.getCurrentTimeStamp() + DEFAULTEXPIRYTIME; 
		this.expires = false;
		this.state = SessionState.ACTIVE;
		this.value = value;
	}
	public void setExpires(boolean expires) {
		this.expires = expires;
	}
	public String getId() {
		return this.getId();
	}
	public Session(String id, String value, Long expiryTimeStamp) {
		this.id = id;
		this.expiryTimeStamp = expiryTimeStamp;
		if (expiryTimeStamp == -1L) {
			this.expires = false;
		}
		this.state = SessionState.ACTIVE;
		this.value = value;
	}
	public boolean updateSession() {
		if ((this.expires) && (this.expiryTimeStamp < Utils.getCurrentTimeStamp())) {
			this.state = SessionState.EXPIRED;
			return false;
		}
		return true;
	}
	public String getValue() {
		this.updateSession();
		if (this.state == SessionState.EXPIRED) {
			System.out.println("Warning: Expired Session Being Read");
		}
		return this.value;
	}
}
