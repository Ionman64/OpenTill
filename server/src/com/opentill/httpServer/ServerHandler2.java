/*package com.opentill.httpServer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentill.logging.Log;
import com.opentill.main.Config;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

import java.io.File;
import java.time.LocalDateTime;

public class ServerHandler2 {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler2.class);

    public static void run() {
    	Server server = null;
    	try {
	    	QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(500); // TODO: Set this in the database
			server = new Server(threadPool);
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
			AuthHandler authHandler = new AuthHandler(sessionHandler, new String[] { "/content/", "/dashboard2.jsp", "/index.jsp", "/login.jsp", "/forgotPassword.jsp", "/resetPassword.jsp", "/thirdParty/", "/api/", "/js/", "/css/", "/views/", "/modals/", "/img/"});
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
			
	        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
	
	        servletContextHandler.setContextPath("/");
	        
	        HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { authContext, documentContext, resourceContext, servletContextHandler, new DefaultHandler() });
			server.setHandler(handlers);
	
	        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
	        servletHolder.setInitOrder(0);
	        servletHolder.setInitParameter(
	                "jersey.config.server.provider.packages",
	                "com.opentill.api"
	        );
	        server.start();
	        Log.info(String.format("Server has been started and can be accessed from %s", Config.getServerUrl()));
	        server.join();
        } catch (Exception ex) {
        	Log.error("Error occurred while starting Jetty");
        	ex.printStackTrace();
            System.exit(1);
	    }
		Log.info("Server Terminated at " + LocalDateTime.now());
        server.destroy();
    }
}*/