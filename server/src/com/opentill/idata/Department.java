package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Utils;

public class Department {
	public static JSONArray getDepartmentId() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray departments = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT :prefix:tblcatagories.id FROM :prefix:tblcatagories WHERE :prefix:tblcatagories.deleted = 0 "
					+ "ORDER BY :prefix:tblcatagories.name ASC");
			pstmt = conn.prepareStatement(sql);
			Log.log(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getString(1));
				departments.add(jo);
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return departments;
	}
	public static JSONArray getDepartments() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray departments = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT :prefix:tblcatagories.id, :prefix:tblcatagories.name, :prefix:tblcatagories.shortHand,"
					+ " FROM :prefix:tblcatagories WHERE :prefix:tblcatagories.deleted = 0 ORDER BY :prefix:tblcatagories.name ASC");
			pstmt = conn.prepareStatement(sql);
			Log.log(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getString(1));
				jo.put("name", rs.getString(2));
				jo.put("shortHand", rs.getString(3));
				departments.add(jo);
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return departments;
	}
	public static JSONArray getDepartmentsWithInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray departments = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT COUNT(:prefix:tblproducts.id) AS \"numOfProducts\", :prefix:tblcatagories.id, :prefix:tblcatagories.shortHand, :prefix:tblcatagories.name, :prefix:tblcatagories.colour, :prefix:tblcatagories.deleted FROM :prefix:tblcatagories LEFT JOIN :prefix:tblproducts ON :prefix:tblcatagories.id = :prefix:tblproducts.department GROUP BY :prefix:tblcatagories.name ORDER BY :prefix:tblproducts.name, :prefix:tblcatagories.deleted ASC");
			pstmt = conn.prepareStatement(sql);

			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("n_products", rs.getInt(1));
				jo.put("id", rs.getString(2));
				jo.put("shorthand", rs.getString(3));
				jo.put("name", rs.getString(4));
				jo.put("colour", rs.getString(5));
				jo.put("deleted", rs.getInt(6));
				departments.add(jo);
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return departments;
	}
}
