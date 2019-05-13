/*package com.opentill.httpServer;

import java.io.File;
import java.net.BindException;
import java.time.LocalDateTime;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;

import com.opentill.logging.Log;
import com.opentill.mail.MailHandler;
import com.opentill.main.Config;

public final class ServerHandler {
	public static void run() {
		// TODO Auto-generated method stub
		try {
			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(500); // TODO: Set this in the database
			Server server = new Server(threadPool);
			boolean debug = false;
			if (!debug) {
				server.setDumpAfterStart(false);
				server.setDumpBeforeStop(false);
			}
			server.setStopAtShutdown(true);

			ServerConnector connector = new ServerConnector(server);
			connector.setPort(Config.PORT);
			server.addConnector(connector);

			CustomSessionHandler sessionHandler = new CustomSessionHandler();

			ContextHandler authContext = new ContextHandler();
			AuthHandler authHandler = new AuthHandler(sessionHandler,
					new String[] { "/content/", "/index.jsp", "/login.jsp", "/forgotPassword.jsp", "/resetPassword.jsp", "/thirdParty/", "/api/", "/js/", "/css/", "/views/", "/modals/", "/img/"});
			authHandler.setAuthPage("login.jsp");
			authContext.setHandler(authHandler);
			
			ContextHandler documentContext = new ContextHandler();
			ResourceHandler documentHandler = new ResourceHandler();
			documentHandler.setDirectoriesListed(false);
			documentHandler.setWelcomeFiles(new String[] { "index.jsp" });
			documentHandler.setBaseResource(Resource.newResource(Config.APP_HOME + File.separatorChar + "temp"));
			documentContext.setHandler(documentHandler);
			documentContext.setContextPath("/temp/*");

			ContextHandler resourceContext = new ContextHandler();
			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setDirectoriesListed(false);
			resourceHandler.setWelcomeFiles(new String[] { "index.jsp" });
			resourceHandler.setResourceBase("content");
			resourceContext.setHandler(resourceHandler);
			resourceContext.setContextPath("*");
			

			ContextHandler apiContext = new ContextHandler();
			apiContext.setContextPath("/api/*");
			apiContext.setHandler(new API(sessionHandler));

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { authContext, documentContext, resourceContext, apiContext, new DefaultHandler() });
			server.setHandler(handlers);

			server.start();
			Log.info("Started: Server Handler @ " + LocalDateTime.now());
			Log.info("Server can be accessed from " + Config.getServerUrl());
			
			Email email = EmailBuilder.startingBlank()
				    .from(String.format("%s Robot", Config.APP_NAME), Config.emailProperties.getProperty("email_user"))
				    .to(null, "Admin@opentill.com")
				    .withSubject(String.format("[%s] Your installation has just started", Config.APP_NAME))
				    .withHTMLText(String.format("Hi, <br><br>Your installation has just started<br><br>Regards,<br>%s", Config.APP_NAME))
				    .buildEmail();
			MailHandler.emails.add(email);
			server.join();
			
			
			 
		} catch (BindException e) {
			Log.critical("Could not connect to port, perhaps it is taken");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.info("Server Terminated at " + LocalDateTime.now());
	}
}*/
