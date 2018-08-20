package com.opentill.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.main.Utils;

/**
 * Class to handle a single Database Migration
 */
public class DatabaseMigration {
	private Float targetVersion = 0.00F;
	public DatabaseMigration(Float version) throws Exception {
		if (version == null) {
			throw new Exception("Version cannot be null");
		}
		this.targetVersion = version;
	}

	private boolean runSQLfile(File sqlFile) throws IOException, SQLException {
		if (!sqlFile.exists()) {
			throw new IOException(sqlFile.getAbsolutePath() + ": file does not exist");
		}
		Connection conn = null;
		Statement stmt = null;
		BufferedReader bf = null;
		try {
			conn = DatabaseHandler.getDatabase();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			bf = new BufferedReader(new FileReader(sqlFile));
			String line = null, old = "";
			line = bf.readLine();
			while (line != null) {
				if (line.endsWith(";")) {
					String prefixedSQL = Utils.addTablePrefix(old + line);
					stmt.executeUpdate(prefixedSQL);
					old = "";
				} else
					old = old + "\n" + line;
				line = bf.readLine();
			}
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		} finally {
			bf.close();
			DatabaseHandler.closeDBResources(null, stmt, conn);
		}
		return true;
	}

	public void up() throws IOException, SQLException {
		if (this.targetVersion >= Config.CURRENT_LOCAL_VERSION) {
			Log.info("Database up to date");
			return;
		}
		File file = Paths.get(Config.APP_HOME, "migrations", String.valueOf(this.targetVersion).replace(".",  "-").concat("-up.sql")).toFile();
		if (!file.exists()) {
			Log.critical("Cannot Find Migration File:" + file.getAbsolutePath());
			return;
		}
		if (this.runSQLfile(file)) {
			Log.info("Database upgraded successfully to version " + this.targetVersion);
			return;
		}
		Log.info("Database could not be upgraded to version " + this.targetVersion);

	}

	public void down() throws IOException, SQLException {
		if (this.targetVersion < Config.CURRENT_LOCAL_VERSION) {
			Log.info("Current database at same version as target version for downgrade");
			return;
		}
		File file = Paths.get(Config.APP_HOME, "migrations", String.valueOf(this.targetVersion).replace(".",  "-").concat("-down.sql")).toFile();
		if (!file.exists()) {
			Log.critical("Cannot Find Migration File:" + file.getAbsolutePath());
			return;
		}
		if (this.runSQLfile(file)) {
			Log.info("Database reverted successfully from version " + this.targetVersion);
			return;
		}
		Log.info("Database could not be reverted from version " + this.targetVersion);
	}
}
