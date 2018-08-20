package com.opentill.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opentill.database.DatabaseMigration;
import com.opentill.logging.Log;

public class Updater {
	public static void update() {
		try {
			URL website = new URL("https://www.goldstandardresearch.co.uk/opentill/opentill.jar");
			File file = new File(
					Config.APP_HOME + File.pathSeparator + "temp" + File.pathSeparator + "new_version.jar");
			FileUtils.copyURLToFile(website, file);
		} catch (IOException e) {
			Log.info("Could not download update file:" + e.getMessage());
		}
	}
	public static boolean updateFromRemoteDatabaseIfNeeded() {
		Log.log("Contacting remote server for updates");
		try {
			JSONParser parser = new JSONParser();
			String serverResp = new HttpConnector().sendPost("https://www.goldstandardresearch.co.uk/kvs/status/version.json", null, "application/json");
			JSONObject jo = (JSONObject) parser.parse(serverResp);
			if (jo.containsKey("version")) {
				Float REMOTE_VERSION = Float.valueOf(((Double) jo.get("version")).toString());
				if (REMOTE_VERSION > Config.CURRENT_LOCAL_VERSION) {
					new DatabaseMigration(REMOTE_VERSION).up();
					return true;
				}
			}
		} catch (Exception e) {
			Log.warn(e.getMessage());
		}
		return false;
	}
}
