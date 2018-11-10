package com.opentill.httpServer;

import java.util.HashMap;
import java.util.Iterator;

import com.opentill.main.Utils;
import com.opentill.models.UserModel;

public class CustomSessionHandler {
	private HashMap<String, UserSession> active = null;
	private int SESSIONTIMEOUT = -1;
	public Long nextCleanUp = Utils.getCurrentTimeStamp();

	public CustomSessionHandler() {
		this.active = new HashMap<String, UserSession>();
	}

	public UserModel getSessionValue(String sessionGuid) {
		// Check session key exists, if it does, check the timeout, then return
		if (sessionGuid == null) {
			return null;
		}
		if (!active.containsKey(sessionGuid)) {
			return null;
		}
		UserSession session = active.get(sessionGuid);
		return session.getValue();
	}

	public String createUserSession(UserModel user) {
		String newSessionGuid = Utils.GUID();
		UserSession newSession = new UserSession(newSessionGuid, user);
		this.active.put(newSessionGuid, newSession);
		return newSessionGuid;
	}

	public void updateSessions() {
		// Remove Expired Sessions
		Iterator<UserSession> sessions = active.values().iterator();
		while (sessions.hasNext()) {
			UserSession session = sessions.next();
			if (session.state != SessionState.ACTIVE) {
				active.remove(session.getId());
			}
		}
	}

	public boolean destroySession(String sessionGuid) {
		if (!active.containsKey(sessionGuid)) {
			return false;
		}
		active.remove(sessionGuid);
		return true;
	}

	public int numberOfActiveSessions() {
		return active.size();
	}
}
