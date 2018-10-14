package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.database.SQLStatement;
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

	public static JSONArray getOperators() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray joArray = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:operators.id, :prefix:operators.name, :prefix:operators.type FROM :prefix:operators WHERE :prefix:operators.deleted = 0 ORDER BY :prefix:operators.name"));
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getString(1));
				jo.put("name", rs.getString(2));
				jo.put("type", rs.getInt(3));
				joArray.add(jo);
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return joArray;
	}
	
	public static String createPasswordResetLink(String userId) throws Exception {
		String id = Utils.GUID().replace("-", "");
		String token = Utils.GUID().replace("-", "");
		SQLStatement sqlStatement = new SQLStatement().insertInto(":prefix:forgotPassword").columns(new String[] {"id", "time", "userId", "token"}).values(new Object[] {id, Utils.getCurrentTimeStamp()/1000, userId, Utils.hashPassword(token, id)});
		if (sqlStatement.construct().executeUpdate()) {
			return String.format(Config.getServerUrl() + "/resetPassword.jsp?id=%s&token=%s", id, token);
		}
		else {
			throw new Exception("Could not create password link");
		}
	}
	
	public static boolean deleteResetPasswordLink(String userId) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				conn = DatabaseHandler.getDatabase();
				pstmt = conn.prepareStatement(Utils.addTablePrefix("DELETE FROM :prefix:forgotPassword WHERE userId=?"));
				pstmt.setString(1, userId);
				if (pstmt.executeUpdate() > 0) {
					return true;
				}
			} catch (Exception ex) {
				Log.info(ex.toString());
			} finally {
				DatabaseHandler.closeDBResources(rs, pstmt, conn);
			}
			return false;		 
	}
	
	public static String getUserIdFromResetPasswordLink(String id, String token) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT userId FROM :prefix:forgotPassword WHERE id = ? AND token = ? AND time > ? LIMIT 1"));
			pstmt.setString(1, id);
			pstmt.setString(2, Utils.hashPassword(token, id));
			pstmt.setLong(3, (Utils.getCurrentTimeStamp()/1000)-Utils.SECONDS_IN_A_DAY);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Operators.deleteResetPasswordLink(rs.getString(1));
				return rs.getString(1);
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
}
