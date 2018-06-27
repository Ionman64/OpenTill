package com.opentill.logging;

public class Log {
	private static String CRITICAL = "CRITICAL";
	private static String ERROR = "ERROR";
	private static String WARN = "WARN";
	private static String INFO = "INFO";
	private static String LOG = "LOG";
	public static void critical(String m) {
		System.out.println(String.format("[%s]\t%s", Log.CRITICAL, m));
	}

	public static void error(String m) {
		System.out.println(String.format("[%s]\t%s", Log.ERROR, m));
	}
	
	public static void warn(String m) {
		System.out.println(String.format("[%s]\t%s", Log.WARN, m));
	}
	
	public static void info(String m) {
		System.out.println(String.format("[%s]\t%s", Log.INFO, m));
	}

	public static void log(String m) {
		System.out.println(String.format("[%s]\t%s", Log.LOG, m));
	}
}
