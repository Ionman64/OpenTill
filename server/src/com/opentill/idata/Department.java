package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Department {
	public static JSONObject getDepartments() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, "
					+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.name FROM " + Config.DATABASE_TABLE_PREFIX
					+ "tblcatagories WHERE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.deleted = 0 ORDER BY "
					+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			rs = pstmt.executeQuery();
			JSONObject departments = new JSONObject();
			while (rs.next()) {
				departments.put(rs.getString(1), rs.getString(2));
			}
			return departments;
		} catch (Exception ex) {
			Log.log(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
}
