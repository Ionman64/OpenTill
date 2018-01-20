package com.opentill.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.opentill.logging.Log;
import com.opentill.main.Config;

public class DatabaseHandler {
	private static String dbms = "mysql";
	public static java.sql.Connection getDatabase() throws SQLException {
	    java.sql.Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", Config.databaseProperties.getProperty("database_user"));
	    connectionProps.put("password", Config.databaseProperties.getProperty("database_password"));
	    conn = DriverManager.getConnection("jdbc:" + DatabaseHandler.dbms + "://" + Config.databaseProperties.getProperty("database_url") + ":" + Config.databaseProperties.getProperty("database_port") + "/" + Config.databaseProperties.getProperty("database_name"), connectionProps);
	    if (conn == null) {
	    	throw new SQLException("Cannot connect to database - Please check configuration");
	    }
	    return conn;
	}
	public static java.sql.Connection getDatabase(String url, int port, String user, String password, String databaseName) throws SQLException {
	    java.sql.Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", user);
	    connectionProps.put("password", password);
	    conn = DriverManager.getConnection("jdbc:" + DatabaseHandler.dbms + "://" + url + ":" + port + "/" + databaseName, connectionProps);
	    if (conn == null) {
	    	throw new SQLException("Cannot connect to database - Please check configuration");
	    }
	    return conn;
	}
}
