package com.opentill.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;

public final class Config {
	public static String USER_HOME = System.getProperty("user.home");
	public final static Properties databaseProperties = Config.readPropertiesFile();
	public final static Properties emailProperties = Config.getEmailProperties();
	public static boolean setup() {
		if (!createEnvironmentIfNotExists()) {
			return false;
		}
		if (!createLogsFolderIfNotExists()) {
			return false;
		}
		if (!createDatabasePropertiesFileIfNotExists()) {
			return false;
		}
		if (!createEmailPropertiesFileIfNotExists()) {
			return false;
		}
		return true;
	}
	private static boolean createEmailPropertiesFileIfNotExists() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "email.properties";
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
				Log.log("Cannot write default props file");
				System.exit(1); //Indicates a terminal fault
			}
		}
		else {
			return true;
		}
		return false;
	}
	private static Properties getEmailProperties() {
		//TODO: Get from Database 
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "email.properties";
		File props_file = new File(props_path);
		if (props_file.exists()) {
			try {
				FileInputStream fileO = new FileInputStream(props_path);
				Properties props = new Properties();
				props.load(fileO);
				fileO.close();
				Log.log("Reading Properties File");
				return props;
			} catch (IOException e) {
				Log.log("Cannot read default props file");
				System.exit(1); //Indicates a terminal fault
			}
		}
		else {
			return null;
		}
		return null;
	}
	private static Properties readPropertiesFile() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "database.properties";
		File props_file = new File(props_path);
		if (props_file.exists()) {
			try {
				FileInputStream fileO = new FileInputStream(props_path);
				Properties props = new Properties();
				props.load(fileO);
				fileO.close();
				Log.log("Reading Properties File");
				return props;
			} catch (IOException e) {
				Log.log("Cannot read default props file");
				System.exit(1); //Indicates a terminal fault
			}
		}
		else {
			return null;
		}
		return null;
	}
	public static boolean createEnvironmentIfNotExists() {
		String env_path = Config.USER_HOME + File.separatorChar + ".opentill";
		File env_directory = new File(env_path);
		if (!env_directory.exists()) {
			return env_directory.mkdirs();
		}
		else {
			return true;
		}
	}
	public static boolean createLogsFolderIfNotExists() {
		String env_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "logs";
		File env_directory = new File(env_path);
		if (!env_directory.exists()) {
			return env_directory.mkdirs();
		}
		else {
			return true;
		}
	}
	public static boolean createDatabasePropertiesFileIfNotExists() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "database.properties";
		File props_file = new File(props_path);
		if (!props_file.exists()) {
			try {
				FileOutputStream fileO = new FileOutputStream(props_path);
				Properties props = new Properties();
				props.setProperty("database_name", "opentill");
				props.setProperty("database_user", "root");
				props.setProperty("database_password", "");
				props.setProperty("database_port", "3306");
				props.setProperty("database_url", "localhost");
				props.store(fileO, "opentill database connection details");
				fileO.close();
				return true;
			} catch (IOException e) {
				Log.log("Cannot write default props file");
				System.exit(1); //Indicates a terminal fault
			}
		}
		else {
			return true;
		}
		return false;
	}
}
