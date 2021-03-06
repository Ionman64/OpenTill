package com.opentill.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.opentill.logging.Log;

public final class Config {
	public static final boolean HTTPS = false;
	public static final int PORT = 8080;
	public static final int DATABASE_PORT = 3306;
	public static final String APP_NAME = "OpenTill";
	public static final Float CURRENT_LOCAL_VERSION = 0.02F;
	public static final String AUTH_COOKIE_NAME = "auth";
	public static final String JSON_PARAMETER_NAME = "json";
	public static final String SERVER_ADDR = "localhost";
	public static final String DATABASE_TABLE_PREFIX = "kvs_";
	public static String OPEN_TILL_URL = Config.getServerUrl();
	public static String USER_HOME = System.getProperty("user.home");
	public static String APP_HOME = Config.USER_HOME + File.separatorChar + ".opentill";
	public final static Properties databaseProperties = Config.readDatabasePropertiesFile();
	public final static Properties emailProperties = Config.getEmailProperties();
	private static final String CONNECTION_POOL_SIZE = "10";
	public static final String CONNECTION_URL = "jdbc:mysql://" + Config.SERVER_ADDR + ":" + Config.DATABASE_PORT + "/" + Config.APP_NAME;

	public static boolean setup() {
		if (!createRootFolderIfNotExists()) {
			return false;
		}
		String[] folders = new String[] {"logs", "temp", "migrations"};
		for (String folder : folders) {
			if (!createFolderIfNotExists(folder)) {
				return false;
			}
		}
		if (!createDatabasePropertiesFileIfNotExists()) {
			return false;
		}
		if (!createEmailPropertiesFileIfNotExists()) {
			return false;
		}
		return true;
	}
	
	public static String getServerUrl() {
		if (Config.HTTPS) {
			return String.format("https://%s:%s", Config.SERVER_ADDR, Config.PORT);
		}
		else {
			return String.format("http://%s:%s", Config.SERVER_ADDR, Config.PORT);
		}
	}

	private static boolean createEmailPropertiesFileIfNotExists() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar
				+ "email.properties";
		File props_file = new File(props_path);
		if (!props_file.exists()) {
			try {
				FileOutputStream fileO = new FileOutputStream(props_path);
				Properties props = new Properties();
				props.setProperty("email_hostname", "send.one.com");
				props.setProperty("email_port", "465");
				props.setProperty("email_user", "robot@goldstandardresearch.co.uk");
				props.setProperty("email_password", "");
				props.store(fileO, "opentill email connection details");
				fileO.close();
				return true;
			} catch (IOException e) {
				Log.info("Cannot write default props file");
				System.exit(1); // Indicates a terminal fault
			}
		} else {
			return true;
		}
		return false;
	}

	private static Properties getEmailProperties() {
		// TODO: Get from Database
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar
				+ "email.properties";
		File props_file = new File(props_path);
		if (props_file.exists()) {
			try {
				FileInputStream fileO = new FileInputStream(props_path);
				Properties props = new Properties();
				props.load(fileO);
				fileO.close();
				return props;
			} catch (IOException e) {
				Log.info("Cannot read default props file");
				System.exit(1); // Indicates a terminal fault
			}
		} else {
			return null;
		}
		return null;
	}

	private static Properties readDatabasePropertiesFile() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar
				+ "database.properties";
		File props_file = new File(props_path);
		if (props_file.exists()) {
			try {
				FileInputStream fileO = new FileInputStream(props_path);
				Properties props = new Properties();
				props.load(fileO);
				fileO.close();
				return props;
			} catch (IOException e) {
				Log.info("Cannot read default props file");
				System.exit(1); // Indicates a terminal fault
			}
		} else {
			return null;
		}
		return null;
	}

	public static boolean createRootFolderIfNotExists() {
		File env_directory = new File(Config.APP_HOME);
		if (!env_directory.exists()) {
			return env_directory.mkdirs();
		} else {
			return true;
		}
	}

	public static boolean createFolderIfNotExists(String foldername) {
		String env_path = Config.APP_HOME + File.separatorChar + foldername;
		File env_directory = new File(env_path);
		if (!env_directory.exists()) {
			return env_directory.mkdirs();
		} else {
			return true;
		}
	}
	
	public static boolean createDatabasePropertiesFileIfNotExists() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar
				+ "database.properties";
		File props_file = new File(props_path);
		if (!props_file.exists()) {
			try {
				FileOutputStream fileO = new FileOutputStream(props_path);
				Properties props = new Properties();
				props.put("hibernate.connection.url", Config.CONNECTION_URL);
				props.put("hibernate.connection.username", "root");
				props.put("hibernate.connection.password", "");
				props.put("hibernate.driver_class", "com.mysql.jdbc.Driver");
				props.put("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
				props.put("hibernate.order_updates", "true");
				props.put("hibernate.show_sql", "true");
				props.put("hibernate.pool_size", Config.CONNECTION_POOL_SIZE);
				props.put("current_session_context_class", "thread");
				props.store(fileO, String.format("%s hibernate database connection details", Config.APP_NAME));
				fileO.close();
				return true;
			} catch (IOException e) {
				Log.info("Cannot write default props file");
				System.exit(1); // Indicates a terminal fault
			}
		} else {
			return true;
		}
		return false;
	}
}
