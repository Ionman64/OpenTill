package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Takings {
	public static JSONObject getTakings(Long start, Long end, String admin, String type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) AS \"date\", " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department, SUM(" + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.price) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "transactions ON " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = " + Config.DATABASE_TABLE_PREFIX + "transactions.id WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0) GROUP BY " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department, DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) ORDER BY DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) DESC");
			pstmt.setLong(1, start);
			pstmt.setLong(2, end);
			pstmt.setString(3, admin);
			pstmt.setString(4, type);
			rs = pstmt.executeQuery();
			JSONObject allDates = new JSONObject();
			while (rs.next()) {
				if (allDates.containsKey(rs.getString(1))) {
					JSONObject date = (JSONObject) allDates.get(rs.getString(1));
					date.put(rs.getString(2), rs.getFloat(3));
				}
				else {
					JSONObject date = new JSONObject();
					date.put(rs.getString(2), rs.getFloat(3));
					allDates.put(rs.getString(1), date);
				}
			}
			return allDates;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return null;
	}
}
