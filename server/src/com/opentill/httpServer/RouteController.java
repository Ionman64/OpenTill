package com.opentill.httpServer;

import java.lang.reflect.Method;

import com.opentill.logging.Log;
import com.opentill.main.Utils;

public class RouteController {
	public String route = "";
	public RouteController() {
		Route route = this.getClass().getAnnotation(Route.class);
		this.route = route.value();
		for (Method method : this.getClass().getMethods()) {
			SubRoute subroute = method.getAnnotation(SubRoute.class);
			if (!Utils.isNull(subroute)) {
				Log.log(subroute.value());
			}
		}
	}
}
