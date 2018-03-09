package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Transaction {
	public static JSONObject getTransactions(Long start, Long end) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			JSONObject jo = new JSONObject();
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "transactions.id AS 'id', " + Config.DATABASE_TABLE_PREFIX + "operators.name AS cashier, (SELECT COUNT(*) FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts WHERE " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = " + Config.DATABASE_TABLE_PREFIX + "transactions.id) AS '#Products', " + Config.DATABASE_TABLE_PREFIX + "transactions.card AS 'card', " + Config.DATABASE_TABLE_PREFIX + "transactions.ended AS 'ended', " + Config.DATABASE_TABLE_PREFIX + "transactions.cashback AS 'cashback', " + Config.DATABASE_TABLE_PREFIX + "transactions.money_given AS 'money_given', " + Config.DATABASE_TABLE_PREFIX + "transactions.payee AS 'payee', " + Config.DATABASE_TABLE_PREFIX + "transactions.type AS 'type', " + Config.DATABASE_TABLE_PREFIX + "transactions.total AS 'total' FROM " + Config.DATABASE_TABLE_PREFIX + "transactions LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "operators ON " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier = " + Config.DATABASE_TABLE_PREFIX + "operators.id WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended BETWEEN ? AND ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) ORDER BY " + Config.DATABASE_TABLE_PREFIX + "transactions.type DESC, " + Config.DATABASE_TABLE_PREFIX + "transactions.ended");
			pstmt.setLong(1, start);
			pstmt.setLong(2, end);
			pstmt.setString(3, "a10f653a-6c20-11e7-b34e-426562cc935f"); // Admin
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("cashier", rs.getString(2));
				jsonObject.put("numProducts", rs.getInt(3));
				jsonObject.put("card", rs.getFloat(4));
				jsonObject.put("ended", rs.getInt(5));
				jsonObject.put("cashback", rs.getFloat(6));
				jsonObject.put("money_given", rs.getFloat(7));
				jsonObject.put("payee", rs.getString(8));
				jsonObject.put("type", rs.getString(9));
				jsonObject.put("total", rs.getFloat(10));
				jo.put(rs.getString(1), jsonObject);
			}
			return jo;
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return null;
	}
}
