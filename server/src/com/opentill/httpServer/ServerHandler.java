package com.opentill.httpServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.opentill.logging.Log;
import com.opentill.mail.MailHandler;

public final class ServerHandler {
	public static void run() {
		// TODO Auto-generated method stub
        try {
        	int port = 8080;
        	QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setMaxThreads(500); //TODO: Set this in the database
        	Server server = new Server(threadPool);
        	boolean debug = false;
        	if (!debug) {
        		server.setDumpAfterStart(false);
        		server.setDumpBeforeStop(false);
        	}
    	    server.setStopAtShutdown(true);
    	    
    	    HttpConfiguration http_config = new HttpConfiguration();
            http_config.setSecureScheme("https");
            http_config.setSendServerVersion(true);
            
            ConstraintSecurityHandler security = new ConstraintSecurityHandler();
            server.setHandler(security);
            
            Constraint constraint = new Constraint();
            constraint.setName("auth");
            constraint.setAuthenticate(true);
            constraint.setRoles(new String[] { "user", "admin" });

            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec("/*");
            mapping.setConstraint(constraint);
            security.setConstraintMappings(Collections.singletonList(mapping));
            security.setAuthenticator(new BasicAuthenticator());
            //security.setLoginService();
            
    	    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
    	    http.setHost("localhost");
            http.setPort(port);
            http.setIdleTimeout(30000);
            server.addConnector(http);
            
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[]{"index.php"});
            resourceHandler.setResourceBase("content");
            security.setHandler(resourceHandler);
            
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath(".");
            webAppContext.setResourceBase("content");
            webAppContext.setInitParameter("dirAllowed", "false");
			new JspStarter(webAppContext).doStart();
			
			HandlerCollection handlers = new HandlerCollection();
		    handlers.addHandler(new API(webAppContext, "/api/kvs.php"));
		    handlers.addHandler(webAppContext);
		    handlers.addHandler(resourceHandler);
		    server.setHandler(handlers);
		    server.start();
		    Log.log("Server Started on PORT:" + port);
		    /*Email email = new SimpleEmail();
			email.setSubject("OpenTill Server Started");
			email.setMsg("Your OpenTill server instance has started at " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
			email.addTo("peterpickerill2014@gmail.com");
			MailHandler.emails.add(email);*/
		    server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
	}
}
