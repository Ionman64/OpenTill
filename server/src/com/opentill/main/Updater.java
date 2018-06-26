package com.opentill.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.opentill.logging.Log;

public class Updater {
	public static void update() {
		try {
			URL website = new URL("https://www.goldstandardresearch.co.uk/opentill/opentill.jar");
			File file = new File(
					Config.APP_HOME + File.pathSeparator + "temp" + File.pathSeparator + "new_version.jar");
			FileUtils.copyURLToFile(website, file);
		} catch (IOException e) {
			Log.log("Could not download update file:" + e.getMessage());
		}
	}

	public static void checkForUpdate() {

	}
}
