/*package com.opentill.api;


import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.opentill.httpServer.Route;
import com.opentill.httpServer.RouteMethod;
import com.opentill.httpServer.SubRoute;

public abstract class BaseController extends AbstractHandler {
	private String route;
	private String regexRoute;
	
	public BaseController() {
		Route route = this.getClass().getAnnotation(Route.class);
		this.route = route.value();
	}
	
	public String getRoute() {
		return route;
	}
	
	private String replaceParamsWithRegexMatches(String input) {
		Pattern p = Pattern.compile("\\{(\\w+|\\d+)\\}");
		Matcher m = p.matcher(input);
		return m.replaceAll("(.*)");
	}
	
	private boolean regexMatch(String regexString, String targetString) {
		Pattern p = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(targetString);
		return m.find();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			for (Method method : this.getClass().getDeclaredMethods()) {
				SubRoute subRoute = method.getAnnotation(SubRoute.class);
				RouteMethod routeMethod = method.getAnnotation(RouteMethod.class);
				String subRouteMethod = null;
				String subRouteString = "";
				if (routeMethod == null) {
					subRouteMethod = RouteMethod.GET;
				}
				else {
					subRouteMethod = routeMethod.value();
				}
				if (!subRouteMethod.equals(request.getMethod())) {
					continue;
				}
				if (subRoute.value() != null) {
					subRouteString = subRoute.value();
				}
				String replacedString = replaceParamsWithRegexMatches(subRouteString);
				if (!regexMatch(replacedString, target)) {
					continue;
				}
				ArrayList<String> params = new ArrayList<String>();
				Pattern p = Pattern.compile("\\{(\\w+|\\d+)\\}");
				Matcher m = p.matcher(subRouteString);
				if (m.find()) {
					int i = 0;
					while (i++ < m.groupCount()) {
						if (m.group(i).equals("")) {
							throw new Exception("Param name cannot be null");
						}
						params.add(m.group(i));
					}
				}
				if (params.isEmpty()) {
					method.invoke(this, target, baseRequest, request, response);
				}
				else {
					method.invoke(this, target, baseRequest, request, response, params);
				}
				
				baseRequest.setHandled(true);
				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}*/
