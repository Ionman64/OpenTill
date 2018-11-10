package com.opentill.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.opentill.main.Config;

public class Log {
	private static String CRITICAL = "CRITICAL";
	private static String ERROR = "ERROR";
	private static String WARN = "WARN";
	private static String INFO = "INFO";
	private static String LOG = "LOG";
	private static String DEBUG = "DEBUG";
	
	private static void writeToFile(String m) {
		System.out.println(m);
		try {
			File file = new File(Config.APP_HOME + File.separatorChar + "logs" + File.separatorChar + getDate() + ".log");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(m + "\n");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static void critical(String m) {
		writeToFile(String.format("[%s][%s]\t%s", Log.CRITICAL, Log.getDateTime(), m));
	}

	public static void error(String m) {
		writeToFile(String.format("[%s][%s]\t%s", Log.ERROR, Log.getDateTime(), m));
	}
	
	public static void warn(String m) {
		writeToFile(String.format("[%s][%s]\t%s", Log.WARN, Log.getDateTime(), m));
	}
	
	public static void info(String m) {
		writeToFile(String.format("[%s][%s]\t%s", Log.INFO, Log.getDateTime(), m));
	}

	public static void log(String m) {
		writeToFile(String.format("[%s][%s]\t%s", Log.LOG, Log.getDateTime(), m));
	}
	
	public static void Debug(String m) {
		writeToFile(String.format("[%s][%s]\t%s", Log.DEBUG, Log.getDateTime(), m));
	}
}
