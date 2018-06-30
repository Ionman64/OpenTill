package com.opentill.httpServer;

import java.util.HashMap;
import java.util.Iterator;

import com.opentill.idata.CustomUser;
import com.opentill.main.Utils;

public class CustomSessionHandler {
	private HashMap<String, Session> active = null;
	private int SESSIONTIMEOUT = -1;
	public Long nextCleanUp = Utils.getCurrentTimeStamp();

	public CustomSessionHandler() {
		this.active = new HashMap<String, Session>();
	}

	public CustomUser getSessionValue(String sessionGuid) {
		// Check session key exists, if it does, check the timeout, then return
		if (sessionGuid == null) {
			return null;
		}
		if (!active.containsKey(sessionGuid)) {
			return null;
		}
		Session session = active.get(sessionGuid);
		return session.getValue();
	}

	public String createUserSession(CustomUser user) {
		String newSessionGuid = Utils.GUID();
		Session newSession = new Session(newSessionGuid, user);
		this.active.put(newSessionGuid, newSession);
		return newSessionGuid;
	}

	public void updateSessions() {
		// Remove Expired Sessions
		Iterator<Session> sessions = active.values().iterator();
		while (sessions.hasNext()) {
			Session session = sessions.next();
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
