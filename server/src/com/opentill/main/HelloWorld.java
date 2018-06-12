package com.opentill.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;

import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import com.opentill.httpServer.API;

public class HelloWorld extends AbstractHandler
{
    @Override
    public void handle( String target,
                        Request baseRequest,
                        HttpServletRequest request,
                        HttpServletResponse response ) throws IOException,
                                                      ServletException
    {
        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8");

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        // Write back response
        response.getWriter().println("<h1>Hello /h1>");
        if (baseRequest.getParameter("ken") != null) {
        	response.getWriter().print(baseRequest.getParameter("ken"));
        }

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    public static void main( String[] args ) throws Exception
    {
    	Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);
 
        ContextHandler resourceContext = new ContextHandler();
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{ "index.jsp" });
        resourceHandler.setResourceBase("content");
        resourceContext.setHandler(resourceHandler);
        resourceContext.setContextPath("*");
        
        
        ContextHandler apiContext = new ContextHandler();
        apiContext.setContextPath("/api");
        apiContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        apiContext.setHandler(new API());
 
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { apiContext, resourceContext, new DefaultHandler() });
        server.setHandler(handlers);
 
        server.start();
        server.join();
    }
}
