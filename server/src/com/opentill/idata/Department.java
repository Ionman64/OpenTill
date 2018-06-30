package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
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
					+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.name, "+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.shortHand, SUM("+ Config.DATABASE_TABLE_PREFIX + "tbl FROM " + Config.DATABASE_TABLE_PREFIX
					+ "tblcatagories WHERE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.deleted = 0 ORDER BY "
					+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			rs = pstmt.executeQuery();
			JSONObject departments = new JSONObject();
			while (rs.next()) {
				departments.put(rs.getString(1), rs.getString(2));
			}
			return departments;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
	public static JSONArray getDepartmentsWithInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department AS \"departmentId\", " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name AS \"departmentName\", " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.colour AS \"colour\", COUNT(" + Config.DATABASE_TABLE_PREFIX + "tblproducts.id) AS \"numOfProducts\" FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblcatagories ON " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department = " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id GROUP BY " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department");
			
			rs = pstmt.executeQuery();
			JSONArray departments = new JSONArray();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getString(1));
				jo.put("name", rs.getString(2));
				jo.put("colour", rs.getString(3));
				jo.put("n_products", rs.getInt(4));
				departments.add(jo);
			}
			return departments;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
}
