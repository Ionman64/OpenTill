package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.main.Utils;
import com.opentill.models.SupplierModel;

public class Supplier {
	public static SupplierModel selectSupplier(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		SupplierModel supplier = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT name, telephone, website, email, comments FROM "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers WHERE id = ? LIMIT 1");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				supplier = new SupplierModel();
				supplier.setId(id);
				supplier.setName(rs.getString(1));
				supplier.setTelephone(rs.getString(2));
				supplier.setWebsite(rs.getString(3));
				supplier.setEmail(rs.getString(4));
				supplier.setComments(rs.getString(5));
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return supplier;
	}

	public static JSONArray getSuppliers() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray joArray = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = "SELECT :prefix:tblsuppliers.id, :prefix:tblsuppliers.name FROM :prefix:tblsuppliers WHERE :prefix:tblsuppliers.deleted = 0 ORDER BY :prefix:tblsuppliers.name";
			pstmt = conn.prepareStatement(Utils.addTablePrefix(sql));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getString(1));
				jo.put("name", rs.getString(2));
				joArray.add(jo);
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return joArray;
	}
}
