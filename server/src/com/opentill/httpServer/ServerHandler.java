package com.opentill.httpServer;

import java.net.BindException;
import java.net.URI;
import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.opentill.logging.Log;
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
					new String[] { "/content/", "/index.jsp", "/login.jsp", "/thirdParty/", "/api/", "/js/", "/css/", "/modals/", "/img/"});
			authHandler.setAuthPage("login.jsp");
			authContext.setHandler(authHandler);

			ContextHandler resourceContext = new ContextHandler();
			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setDirectoriesListed(false);
			resourceHandler.setWelcomeFiles(new String[] { "index.jsp" });
			ClassLoader cl = ServerHandler.class.getClassLoader();
			URL f = cl.getResource("content/index.jsp");
		    if (f == null)
		    {
		        throw new RuntimeException("Unable to find resource directory");
		    }
		    URI webRootUri = f.toURI().resolve("./").normalize();
			resourceHandler.setBaseResource(Resource.newResource(webRootUri));
			resourceContext.setHandler(resourceHandler);
			resourceContext.setContextPath("*");

			/*
			 * ContextHandler staticFileContext = new ContextHandler(); ResourceHandler
			 * staticFileHandler = new ResourceHandler();
			 * staticFileHandler.setDirectoriesListed(false);
			 * //resourceHandler.setWelcomeFiles(new String[]{ "index.jsp" });
			 * staticFileHandler.setResourceBase(Config.APP_HOME + File.separatorChar);
			 * staticFileContext.setHandler(staticFileHandler);
			 * staticFileContext.setContextPath("/temp/*");
			 */

			ContextHandler apiContext = new ContextHandler();
			apiContext.setContextPath("/api");
			apiContext.setClassLoader(Thread.currentThread().getContextClassLoader());
			apiContext.setHandler(new API(sessionHandler));

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { authContext, resourceContext, apiContext, new DefaultHandler() });
			server.setHandler(handlers);

			server.start();
			server.join();

			Log.log("Server Started on PORT:" + Config.PORT);
			/*
			 * Email email = new SimpleEmail(); email.setSubject("OpenTill Server Started");
			 * email.setMsg("Your OpenTill server instance has started at " + new
			 * SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))
			 * ; email.addTo("peterpickerill2014@gmail.com"); MailHandler.emails.add(email);
			 */
		} catch (BindException e) {
			System.out.println("Could not connect to port, perhaps it is taken");
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
