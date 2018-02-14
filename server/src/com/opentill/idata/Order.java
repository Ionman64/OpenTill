package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Order {
	public static JSONObject getOrders() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblorders.id, " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name FROM " + Config.DATABASE_TABLE_PREFIX + "tblorders LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers ON " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.id = " + Config.DATABASE_TABLE_PREFIX + "tblorders.supplier WHERE " + Config.DATABASE_TABLE_PREFIX + "tblorders.ended = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				jo.put(rs.getString(1), rs.getString(2));
			}
			return jo;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
}
