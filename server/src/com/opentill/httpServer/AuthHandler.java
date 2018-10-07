package com.opentill.httpServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.opentill.main.Config;

public class AuthHandler extends AbstractHandler {
	CustomSessionHandler sessionHandler = null;
	String authPage = null;
	String[] allowedDomains = null;
	String authContext;

	public AuthHandler(CustomSessionHandler sessionHandler, String[] allowedDomains) {
		this.sessionHandler = sessionHandler;
		this.allowedDomains = allowedDomains;
	}

	public void setAuthPage(String authPage) {
		this.authPage = authPage;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String sessionId = null;
		for (String domain : allowedDomains) {
			if (domain.endsWith("/") && target.startsWith(domain)) {
				baseRequest.setHandled(false);
				return;
			} else if (target.equals(domain)) {
				baseRequest.setHandled(false);
				return;
			}
		}
		if ((request.getCookies() != null) && (request.getCookies().length > 0)) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(Config.AUTH_COOKIE_NAME)) {
					sessionId = cookie.getValue();
				}
			}
		}
		if ((sessionId == null) || (sessionHandler.getSessionValue(sessionId) == null)) {
			response.sendRedirect(this.authPage);
			baseRequest.setHandled(true);
		} else {
			baseRequest.setHandled(false);
		}
	}

}
