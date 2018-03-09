package com.opentill.httpServer;

import java.util.Collections;

import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
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
            holderHome.setWelcomeFiles(new String[]{"index.jsp"});
            holderHome.setPathInfoOnly(true);
            holderHome.setResourceBase(Config.APP_HOME);
            
            
    	    ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
    	    http.setHost("localhost");
            http.setPort(port);
            http.setIdleTimeout(30000);
            server.addConnector(http);
           
            
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath(".");
            webAppContext.setResourceBase("content");
            webAppContext.setInitParameter("dirAllowed", "false");
			new JspStarter(webAppContext).doStart();
			
			LoginService loginService = new HashLoginService();
			
			
			
            server.addBean(loginService);
            
            HandlerCollection handlers = new HandlerCollection();
		    handlers.addHandler(new API(webAppContext, "/api/kvs.php"));
		    
		    handlers.addHandler(webAppContext);
		    handlers.addHandler(holderHome);
            
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
	
	private static LoginService getLoginService(){
	    // HASH LOGIN SERVICE
	    HashLoginService loginService = new HashLoginService("admin");
	    UserStore userStore = new UserStore();
	    Credential cred = Credential.getCredential("password");
	    userStore.addUser("peterpickerill2016@gmail.com", cred, new String[] {"user"});
	    loginService.setUserStore(userStore);
	    return loginService;
	}

	private static Authenticator getAuthenticator() {
		// TODO Auto-generated method stub
		return new FormAuthenticator("/login.jsp", "/login.jsp", false);
	}
}
