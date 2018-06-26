package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Supplier {
	public static JSONObject selectSupplier(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT name, telephone, website, email, comments FROM "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers WHERE id = ? LIMIT 1");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			if (rs.next()) {
				jo.put("name", rs.getString(1));
				jo.put("telephone", rs.getString(2));
				jo.put("website", rs.getString(3));
				jo.put("email", rs.getString(4));
				jo.put("comments", rs.getString(5));
			}
			return jo;
		} catch (Exception ex) {
			Log.log(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}

	public static JSONObject getSuppliers() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.id, "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name FROM " + Config.DATABASE_TABLE_PREFIX
					+ "tblsuppliers WHERE " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.deleted = 0 ORDER BY "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				jo.put(rs.getString(1), rs.getString(2));
			}
			return jo;
		} catch (Exception ex) {
			Log.log(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
}
