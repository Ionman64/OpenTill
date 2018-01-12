package com.opentill.main;

import com.opentill.httpServer.ServerHandler;
import com.opentill.logging.*;
import com.opentill.mail.EmailMessage;
import com.opentill.mail.MailHandler;

import javax.servlet.Servlet;

import org.apache.jasper.servlet.JasperInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {
		public static void main(String[] args) throws Exception
	    {
			MailHandler mailHandler = new MailHandler("peterpickerill2014@gmail.com", "pjp104104");
			Thread thread = new Thread(mailHandler);
			thread.run();
			EmailMessage email = new EmailMessage();
			email.setBody("Hello");
			email.addRecipient("peterpickerill2016@gmail.com");
			email.setSubject("Hello");
			mailHandler.addMail(email);
			
			ServerHandler.run();
	    }
}
