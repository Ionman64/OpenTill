package com.opentill.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.opentill.main.Config;

public class DatabaseHandler {
	private static String dbms = "mysql";

	public static Connection getDatabase() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", Config.databaseProperties.getProperty("database_user"));
		connectionProps.put("password", Config.databaseProperties.getProperty("database_password"));
		conn = DriverManager.getConnection(
				"jdbc:" + DatabaseHandler.dbms + "://" + Config.databaseProperties.getProperty("database_url") + ":"
						+ Config.databaseProperties.getProperty("database_port") + "/"
						+ Config.databaseProperties.getProperty("database_name"),
				connectionProps);
		if (conn == null) {
			throw new SQLException("Cannot connect to database - Please check configuration");
		}
		return conn;
	}

	public static Connection getDatabase(String url, int port, String user, String password, String databaseName)
			throws SQLException {
		java.sql.Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		conn = DriverManager.getConnection(
				"jdbc:" + DatabaseHandler.dbms + "://" + url + ":" + port + "/" + databaseName, connectionProps);
		if (conn == null) {
			throw new SQLException("Cannot connect to database - Please check configuration");
		}
		return conn;
	}

	public static Connection getMySqlConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", Config.databaseProperties.getProperty("database_user"));
		connectionProps.put("password", Config.databaseProperties.getProperty("database_password"));
		conn = DriverManager.getConnection(
				"jdbc:" + DatabaseHandler.dbms + "://" + Config.databaseProperties.getProperty("database_url") + ":"
						+ Config.databaseProperties.getProperty("database_port"),
				connectionProps);
		if (conn == null) {
			throw new SQLException("Cannot connect to database - Please check configuration");
		}
		return conn;
	}

	public static void closeDBResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (pstmt != null && !pstmt.isClosed()) {
				pstmt.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeDBResources(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
