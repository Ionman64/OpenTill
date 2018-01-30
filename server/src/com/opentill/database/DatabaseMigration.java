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

/** 
 * Class to handle a single Database Migration
 */
public class DatabaseMigration {
	private String version = null;
	public DatabaseMigration(String version) throws Exception {
		if (version == null) {
			throw new Exception("Version cannot be null");
		}
		this.version = version.replace('.', '-');
	}
	private boolean runSQLfile(File sqlFile) throws IOException, SQLException {
		if (!sqlFile.exists()) {
			throw new IOException(sqlFile.getAbsolutePath() + "file does not exist");
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DatabaseHandler.getMySqlConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			BufferedReader bf = new BufferedReader(new FileReader(sqlFile));
            String line = null,old="";
            line = bf.readLine();
            while (line != null) {
                //q = q + line + "\n";
                if(line.endsWith(";")){
                	stmt.executeUpdate(old+line);
                    old="";
                }
                else
                    old=old+"\n"+line;
                line = bf.readLine();
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			conn.rollback();
			return false;
		}
		finally {
			DatabaseHandler.closeDBResources(null, stmt, conn);
		}
		return true;
	}
	public void up() throws IOException, SQLException  {
		File file = Paths.get("migrations", this.version, "up.sql").toFile();
		if (this.runSQLfile(file)) {
			Log.log("Database upgraded successfully to version " + this.version);
			return;
		}
		Log.log("Database could not be upgraded to version " + this.version);
		
	}
	public void down() throws IOException, SQLException {
		File file = Paths.get("migrations", this.version, "down.sql").toFile();
		if (this.runSQLfile(file)) {
			Log.log("Database reverted successfully from version " + this.version);
			return;
		}
		Log.log("Database could not be reverted from version " + this.version);
	}
}
