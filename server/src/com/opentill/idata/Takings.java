package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Utils;

public class Takings {
	public static JSONObject getTakings(Long start, Long end, String admin, String type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = Utils.addTablePrefix("SELECT DATE(FROM_UNIXTIME(:prefix:transactions.ended)) AS \"date\", "
					+ ":prefix:transactiontoproducts.department, SUM(:prefix:transactiontoproducts.price) AS \"amount\" "
					+ "FROM :prefix:transactiontoproducts LEFT JOIN :prefix:transactions ON :prefix:transactiontoproducts.transaction_id = :prefix:transactions.id "
					+ "WHERE (:prefix:transactions.ended BETWEEN ? AND ?) AND (:prefix:transactions.type = ?) "
					+ "GROUP BY :prefix:transactiontoproducts.department, DATE(FROM_UNIXTIME(:prefix:transactions.ended)) "
					+ "ORDER BY DATE(FROM_UNIXTIME(:prefix:transactions.ended)) DESC");
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, start);
			pstmt.setLong(2, end);
			pstmt.setString(3, type);
			rs = pstmt.executeQuery();
			JSONObject allDates = new JSONObject();
			while (rs.next()) {
				if (allDates.containsKey(rs.getString(1))) {
					JSONObject date = (JSONObject) allDates.get(rs.getString(1));
					date.put(rs.getString(2), rs.getFloat(3));
				} else {
					JSONObject date = new JSONObject();
					date.put(rs.getString(2), rs.getFloat(3));
					allDates.put(rs.getString(1), date);
				}
			}
			return allDates;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return null;
	}
}
