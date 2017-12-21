package com.opentill.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseHandler {
	private static String name = "opentill";
	private static String user = "peter";
	private static String password = "";
	private static String url = "127.0.0.1";
	private static String port = "3306";
	private static String dbms = "mysql";
	public static java.sql.Connection getDatabase() throws SQLException {
	    java.sql.Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", DatabaseHandler.user);
	    connectionProps.put("password", DatabaseHandler.password);
	    conn = DriverManager.getConnection("jdbc:" + DatabaseHandler.dbms + "://" + DatabaseHandler.url + ":" + DatabaseHandler.port + "/" + DatabaseHandler.name, connectionProps);
	    System.out.println("Connected to database");
	    return conn;
	}
}
