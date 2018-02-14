package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Inventory {
	public static JSONObject get_product_levels() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblproducts.id, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.current_stock, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.max_stock, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.colour, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblcatagories ON " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department = " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id WHERE " + Config.DATABASE_TABLE_PREFIX + "tblproducts.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				JSONObject department;
				if (!jo.containsKey(rs.getString(6))) {
					department = new JSONObject();
					department.put("colour", rs.getString(5));
					jo.put(rs.getString(6), department);
				}
				else {
					department = (JSONObject) jo.get(rs.getString(6));
				}
				JSONObject product = new JSONObject();
				product.put("id", rs.getString(1));
				product.put("name",  rs.getString(2));
				product.put("current_stock", rs.getString(3));
				product.put("max_stock", rs.getString(4));
				department.put(rs.getString(1), product);
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
