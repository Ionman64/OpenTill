package com.opentill.httpServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import com.opentill.logging.Log;

public final class ServerHandler {
	public static final void run() {
		// TODO Auto-generated method stub
        try {
        	int port = 8080;
        	Server server = new Server();
    	    server.setStopAtShutdown(true);
    	    ServerConnector http = new ServerConnector(server);
    	    http.setHost("localhost");
            http.setPort(port);
            http.setIdleTimeout(30000);
            server.addConnector(http);
            
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[]{"index.php"});
            resourceHandler.setResourceBase("content");
            
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
		    server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
	}
}
