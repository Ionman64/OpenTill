package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.main.Utils;

public class Operators {
	public static boolean createOperator(String name, String password, String email, String telephone,
			String comments) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String guid = Utils.GUID();
			String code = Utils.GUID().split("-")[0];
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX
					+ "operators (id, code, name, passwordHash, telephone, email, comments, created, updated) VALUES (?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, guid);
			pstmt.setString(2, code);
			pstmt.setString(3, name);
			pstmt.setString(4, Utils.hashPassword(password, ""));
			pstmt.setString(5, telephone);
			pstmt.setString(6, email);
			pstmt.setString(7, comments);
			pstmt.setLong(8, Utils.getCurrentTimeStamp());
			pstmt.setLong(9, Utils.getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				return true;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return false;
	}

	public static JSONObject getOperator(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT name, telephone, email, comments, code FROM "
					+ Config.DATABASE_TABLE_PREFIX + "operators WHERE id = ? LIMIT 1");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			if (rs.next()) {
				jo.put("name", rs.getString(1));
				jo.put("telephone", rs.getString(2));
				jo.put("email", rs.getString(3));
				jo.put("comments", rs.getString(4));
				jo.put("code", rs.getString(5));
			}
			return jo;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}

	public static JSONObject getOperators() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "operators.id, "
					+ Config.DATABASE_TABLE_PREFIX + "operators.name FROM " + Config.DATABASE_TABLE_PREFIX
					+ "operators WHERE " + Config.DATABASE_TABLE_PREFIX + "operators.deleted = 0 ORDER BY "
					+ Config.DATABASE_TABLE_PREFIX + "operators.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				JSONObject product = new JSONObject();
				product.put("name", rs.getString(2));
				jo.put(rs.getString(1), product);
			}
			return jo;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
}
