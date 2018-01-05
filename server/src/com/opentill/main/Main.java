package com.opentill.main;

import com.opentill.logging.*;

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
		public static final int PORT = 8080;
		public static void main(String[] args) throws Exception
	    {
			//System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
			Server server = new Server();
		    server.setStopAtShutdown(true);
		    SessionIdManager sessionManager = server.getSessionIdManager();
		    ServerConnector http = new ServerConnector(server);
		    http.setHost("localhost");
	        http.setPort(8080);
	        http.setIdleTimeout(30000);
	        // Set the connector
	        server.addConnector(http);
	        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
	        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
	       
	   
	        //#context1.setContextPath("/api/kvs.php");
	        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
	        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
	        ResourceHandler resourceHandler = new ResourceHandler();
	        resourceHandler.setDirectoriesListed(true);
	        resourceHandler.setWelcomeFiles(new String[]{"index.php"});
	        resourceHandler.setResourceBase("content");
	        
	        WebAppContext webAppContext = new WebAppContext();
	        webAppContext.setContextPath(".");
	        webAppContext.setResourceBase("content");
	        webAppContext.setInitParameter("dirAllowed", "false");

	        new JspStarter(webAppContext).doStart();
	        
	        //webAppContext.setServer(server);
	        // Add the ResourceHandler to the server.
	        HandlerCollection handlers = new HandlerCollection();
	        handlers.addHandler(new API(webAppContext, "/api/kvs.php"));
	        handlers.addHandler(webAppContext);
	        
	        handlers.addHandler(resourceHandler);
	        
	        
	        
	        
	        
	        
	        server.setHandler(handlers);
	        // Start things up! By using the server.join() the server thread will join with the current thread.
	        // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
	        server.start();
	        server.join();
	        // Set a handler
//		    server.setHandler(new Application());
//		    server.start();
//		    Log.log("Server Started on PORT:" + PORT);
//		    server.join();
	    }
}
