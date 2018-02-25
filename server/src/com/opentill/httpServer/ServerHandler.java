package com.opentill.httpServer;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.opentill.logging.Log;
import com.opentill.main.Config;

public final class ServerHandler {
	public static void run() {
		// TODO Auto-generated method stub
        try {
        	int port = 8000;
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
            
            ResourceHandler holderHome = new ResourceHandler();
            holderHome.setDirAllowed(false);
            holderHome.setPathInfoOnly(true);
            holderHome.setResourceBase(Config.APP_HOME);
            
            
    	    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
    	    http.setHost("localhost");
            http.setPort(port);
            http.setIdleTimeout(30000);
            server.addConnector(http);
           
            String webDir = ServerHandler.class.getProtectionDomain()
                    .getCodeSource().getLocation().toExternalForm();
            
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath(".");
            webAppContext.setWelcomeFiles(new String[]{"index.php"});
            webAppContext.setResourceBase("content");
            webAppContext.setInitParameter("dirAllowed", "false");
			new JspStarter(webAppContext).doStart();
			
			HandlerCollection handlers = new HandlerCollection();
		    handlers.addHandler(new API(webAppContext, "/api/kvs.php"));
		    handlers.addHandler(holderHome);
		    handlers.addHandler(webAppContext);
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
