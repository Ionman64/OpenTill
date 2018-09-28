package com.opentill.idata;

import java.util.HashMap;

public class TakingsRequest {
	private Long start = null;
	private Long end = null;
	private HashMap<String, Long> takings = new HashMap<String, Long>();
	public TakingsRequest(Long startTime, Long endTime, String[] departments) {
		/*this.start = start;
		this.end = end;
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX
					+ "transactions.ended)) AS \"date\", " + Config.DATABASE_TABLE_PREFIX
					+ "transactiontoproducts.department, SUM(" + Config.DATABASE_TABLE_PREFIX
					+ "transactiontoproducts.price) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX
					+ "transactiontoproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "transactions ON "
					+ Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = "
					+ Config.DATABASE_TABLE_PREFIX + "transactions.id WHERE (" + Config.DATABASE_TABLE_PREFIX
					+ "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND "
					+ Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND "
					+ Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX
					+ "transactions.ended > 0) GROUP BY " + Config.DATABASE_TABLE_PREFIX
					+ "transactiontoproducts.department, DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX
					+ "transactions.ended)) ORDER BY DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX
					+ "transactions.ended)) DESC");
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			LinkedHashMap<String, HashMap<String, Double>> allDates = new LinkedHashMap<String, HashMap<String, Double>>();
			while (rs.next()) {
				if (allDates.containsKey(rs.getString(1))) {
					HashMap<String, Double> date = allDates.get(rs.getString(1));
					date.put(rs.getString(2), rs.getDouble(3));
				} else {
					HashMap<String, Double> date = new HashMap<String, Double>();
					date.put(rs.getString(2), rs.getDouble(3));
					if (Arrays.asList(departments).indexOf(rs.getString(2)) > -1) {
						allDates.put(rs.getString(1), date);
					}
				}
			}
			pstmt.close();
			rs.close();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, "
					+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.name FROM " + Config.DATABASE_TABLE_PREFIX
					+ "tblcatagories WHERE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.deleted = 0 ORDER BY "
					+ Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			rs = pstmt.executeQuery();
			HashMap<String, String> departmentsToNames = new HashMap<String, String>();
			while (rs.next()) {
				departmentsToNames.put(rs.getString(1), rs.getString(2));
			}
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
			ExcelHelper excel = new ExcelHelper();
			File file = excel.createTakingsReport(departmentsToNames, departments, allDates);
			if (file == null) {
				errorOut(response);
				return;
			}
			JSONObject responseJo = new JSONObject();
			responseJo.put("success", true);
			responseJo.put("file", String.format("%s/temp/%s", Config.OPEN_TILL_URL, file.getName()));
			response.getWriter().write(responseJo.toJSONString());
			return;*/
	}
	
}
