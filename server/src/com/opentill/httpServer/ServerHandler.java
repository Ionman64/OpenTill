package com.opentill.httpServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
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

    	    
    	    
            ResourceHandler holderHome = new ResourceHandler();
            holderHome.setDirAllowed(false);
            holderHome.setWelcomeFiles(new String[]{"index.jsp"});
            holderHome.setPathInfoOnly(true);
            holderHome.setResourceBase("content");
 
            
//            HttpConfiguration http_config = new HttpConfiguration();
//            http_config.setSecureScheme("https");
//            http_config.setSendServerVersion(true);
//    	    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
//    	    http.setHost("localhost");
//            http.setPort(port);
//            http.setIdleTimeout(30000);
//            server.addConnector(http);
            
            //ServletContext api = new ServletContextHandler(ServletContextHandler.SESSIONS);
            //api.setContextPath("/");
            //server.setHandler(context);
            //api.addServlet(new ServletHolder(new API()),"/api/*");
           
            
//            WebAppContext webAppContext = new WebAppContext();
//            webAppContext.setContextPath(".");
//            webAppContext.setResourceBase("content");
//            webAppContext.setInitParameter("dirAllowed", "false");
            
            HandlerCollection handlers = new HandlerCollection();
            handlers.addHandler(holderHome);
		    //handlers.addHandler(api);
		    
		    //handlers.addHandler(webAppContext);
		    
            
            /*ConstraintSecurityHandler security = new ConstraintSecurityHandler();
            
            //security.setAuthMethod(authMethod);
            security.setLoginService(getLoginService());
            security.setAuthenticator(getAuthenticator());
            security.setCheckWelcomeFiles(true);
            security.setHandler(handlers);
            security.setCheckWelcomeFiles(true);
            
            
            Constraint constraint = new Constraint();
            constraint.setName("user");
            constraint.setAuthenticate(true);
            constraint.setRoles(new String[] { "user", "admin" });
            
            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec("/dashboard.jsp");
            mapping.setConstraint(constraint);*/
            
            //security.setConstraintMappings(Collections.singletonList(mapping));
            //security.setAuthenticator(new BasicAuthenticator());
            //security.setLoginService(loginService);
	            
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
