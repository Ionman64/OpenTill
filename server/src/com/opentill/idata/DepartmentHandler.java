package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Utils;
import com.opentill.models.DepartmentModel;

public class DepartmentHandler {
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
	public static DepartmentModel[] getDepartments() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DepartmentModel[] departments = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT :prefix:tblcatagories.id, :prefix:tblcatagories.name, :prefix:tblcatagories.shortHand,"
					+ " FROM :prefix:tblcatagories WHERE :prefix:tblcatagories.deleted = 0 ORDER BY :prefix:tblcatagories.name ASC");
			pstmt = conn.prepareStatement(sql);
			Log.log(sql);
			rs = pstmt.executeQuery();
			departments = new DepartmentModel[rs.getFetchSize()];
			int i = 0;
			while (rs.next()) {
				DepartmentModel department = new DepartmentModel();
				department.setId(rs.getString(1));
				department.setName(rs.getString(2));
				department.setShortHand(rs.getString(3));
				departments[i++] = department;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return departments;
	}
	public static DepartmentModel[] getDepartmentsWithInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DepartmentModel[] departments = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT :prefix:tblcatagories.id, :prefix:tblcatagories.shortHand, :prefix:tblcatagories.name, :prefix:tblcatagories.colour, :prefix:tblcatagories.deleted FROM :prefix:tblcatagories GROUP BY :prefix:tblcatagories.name ORDER BY :prefix:tblcatagories.deleted ASC");
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				DepartmentModel department = new DepartmentModel();
				department.setId(rs.getString(1));
				department.setShortHand(rs.getString(2));
				department.setName(rs.getString(3));
				department.setColour(rs.getString(4));
				department.setDeleted(rs.getBoolean(5));
				departments[i++] = department;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return departments;
	}
}
