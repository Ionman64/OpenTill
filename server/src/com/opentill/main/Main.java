package com.opentill.main;

import com.opentill.logging.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class Main {
		public static final int PORT = 8080;
		public static void main( String[] args ) throws Exception
		{
		    Server server = new Server();
		    server.setStopAtShutdown(true);
		    ServerConnector http = new ServerConnector(server);
		    http.setHost("localhost");
	        http.setPort(8080);
	        http.setIdleTimeout(30000);
	        // Set the connector
	        server.addConnector(http);
	        // Set a handler
		    server.setHandler(new Application());
		    server.start();
		    Log.log("Server Started on PORT:" + PORT);
		    server.join();
		}
}
