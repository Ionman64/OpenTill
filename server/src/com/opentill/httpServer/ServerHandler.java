package com.opentill.httpServer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.opentill.logging.Log;

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

            ServerConnector connector = new ServerConnector(server);
            connector.setPort(port);
            server.addConnector(connector);
            
            CustomSessionHandler sessionHandler = new CustomSessionHandler();
            
            ContextHandler authContext = new ContextHandler();
            AuthHandler authHandler = new AuthHandler(sessionHandler, new String[] {"/index.jsp", "/login.jsp", "/thirdParty/", "/api/", "/js/", "/css/"});
            authHandler.setAuthPage("login.jsp");
            authContext.setHandler(authHandler);
            authContext.setContextPath("*");
           
            ContextHandler resourceContext = new ContextHandler();
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(false);
            resourceHandler.setWelcomeFiles(new String[]{ "index.jsp" });
            resourceHandler.setResourceBase("content");
            resourceContext.setHandler(resourceHandler);
            resourceContext.setContextPath("*");
            
            /*ContextHandler staticFileContext = new ContextHandler();
            ResourceHandler	staticFileHandler = new ResourceHandler();
            staticFileHandler.setDirectoriesListed(false);
            //resourceHandler.setWelcomeFiles(new String[]{ "index.jsp" });
            staticFileHandler.setResourceBase(Config.APP_HOME + File.separatorChar);
            staticFileContext.setHandler(staticFileHandler);
            staticFileContext.setContextPath("/temp/*");*/
            
            
            
            ContextHandler apiContext = new ContextHandler();
            apiContext.setContextPath("/api");
            apiContext.setClassLoader(Thread.currentThread().getContextClassLoader());
            apiContext.setHandler(new API(sessionHandler));
     
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] {authContext, resourceContext, apiContext, new DefaultHandler()});
            server.setHandler(handlers);
     
            server.start();
            server.join();
            
		    Log.log("Server Started on PORT:" + port);
		    /*Email email = new SimpleEmail();
			email.setSubject("OpenTill Server Started");
			email.setMsg("Your OpenTill server instance has started at " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
			email.addTo("peterpickerill2014@gmail.com");
			MailHandler.emails.add(email);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
	}
}
