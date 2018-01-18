package com.opentill.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.opentill.logging.Log;

public final class Config {
	public static String USER_HOME = System.getProperty("user.home");
	public static boolean setup() {
		if (!createEnvironmentIfNotExists()) {
			return false;
		}
		if (!createLogsFolderIfNotExists()) {
			return false;
		}
		if (!createPropertiesFileIfNotExists()) {
			return false;
		}
		if (!readPropertiesFile()) {
			return false;
		}
		return true;
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
	public static boolean createPropertiesFileIfNotExists() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "database.properties";
		File props_file = new File(props_path);
		if (!props_file.exists()) {
			try {
				FileOutputStream fileO = new FileOutputStream(props_path);
				Properties props = new Properties();
				props.setProperty("database_name", "opentill");
				props.setProperty("database_user", "root");
				props.setProperty("database_password", "");
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
	public static boolean readPropertiesFile() {
		String props_path = Config.USER_HOME + File.separatorChar + ".opentill" + File.separatorChar + "database.properties";
		File props_file = new File(props_path);
		if (props_file.exists()) {
			try {
				FileInputStream fileO = new FileInputStream(props_path);
				Properties props = new Properties();
				props.load(fileO);
				fileO.close();
				//System.setProperties(props);
				return true;
			} catch (IOException e) {
				Log.log("Cannot read default props file");
				System.exit(1); //Indicates a terminal fault
			}
		}
		else {
			return false;
		}
		return false;
	}
}
