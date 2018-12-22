package com.opentill.httpServer;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RouteMethod {
	public static String GET = "GET";
	public static String POST = "POST";
	public static String PUT = "PUT";
	public static String DELETE = "DELETE";
	public static String NONE = "NONE";
	String value() default "";
}


