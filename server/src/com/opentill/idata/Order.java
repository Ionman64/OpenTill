package com.opentill.idata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.document.PDFHelper;
import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.main.Utils;
import com.opentill.models.SupplierModel;
import com.opentill.products.OrderProduct;

public class Order {
	public static JSONObject getOrders() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblorders.id, "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name FROM " + Config.DATABASE_TABLE_PREFIX
					+ "tblorders LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers ON "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers.id = " + Config.DATABASE_TABLE_PREFIX
					+ "tblorders.supplier WHERE " + Config.DATABASE_TABLE_PREFIX + "tblorders.ended = 0 ORDER BY "
					+ Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				jo.put(rs.getString(1), rs.getString(2));
			}
			return jo;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
	public static void getOrderForSupplier(String supplierId) {
		ArrayList<OrderProduct> products = new ArrayList<OrderProduct>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT id, name, (max_stock-current_stock) AS 'order_amount' FROM :prefix:tblproducts WHERE supplier = ? AND (max_stock-current_stock) > 0 ORDER BY department, name"));
			pstmt.setString(1,  supplierId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				OrderProduct product = new OrderProduct();
				product.id = rs.getString(1);
				product.name = rs.getString(2);
				product.order_stock = rs.getInt(3);
				products.add(product);
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		SupplierModel supplier = Supplier.selectSupplier(supplierId);
		PDFHelper.createOrderSheet(Utils.GUID(), supplier.name, products);
	}
}
