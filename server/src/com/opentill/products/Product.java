package com.opentill.products;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Utils;

public class Product {
	public String id = null;
	public String name = null;
	public String barcode = null;
	public int current_stock = 0;
	public int max_stock = 0;
	public BigDecimal price = new BigDecimal(0);
	public boolean autoPricing = false;
	public Float supplierPrice = 0.0F;
	public int unitsInCase = 0;
	public boolean includesVAT = false;
	public Float VATamount = 0.0F;
	public boolean targetPercentage = false;
	public Float targetProfitMargin = 0.0F;
	public String department = null;
	public boolean labelPrinted = false;
	public boolean isCase = false;
	public int units = 0;
	public String unitType = null;
	public int updated = 0;
	public int created = 0;
	public boolean deleted = false;
	public int status = 0;
	
	public static Product getProduct(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblproducts.id, :prefix:tblproducts.name, :prefix:tblproducts.price, " 
					+ ":prefix:tblproducts.barcode, :prefix:tblproducts.department, :prefix:tblproducts.labelPrinted, "
					+ ":prefix:tblproducts.isCase, :prefix:tblproducts.units, "
					+ ":prefix:tblproducts.unitType, :prefix:tblproducts.updated, "
					+ ":prefix:tblproducts.created, :prefix:tblproducts.deleted, :prefix:tblproducts.status, :prefix:tblproducts.max_stock,  "
					+ ":prefix:tblproducts.current_stock FROM :prefix:tblproducts WHERE id = ? LIMIT 1"));
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Product product = new Product();
				product.id = rs.getString(1);
				product.name = rs.getString(2);
				product.price = rs.getBigDecimal(3);
				product.barcode = rs.getString(4);
				product.department = rs.getString(5);
				product.labelPrinted = rs.getInt(6) == 0 ? false : true;
				product.isCase = rs.getInt(7) == 0 ? false : true;
				product.units = rs.getInt(8);
				product.unitType = rs.getString(9);
				product.updated = rs.getInt(10);
				product.created = rs.getInt(11);
				product.deleted = rs.getInt(12) == 0 ? false : true;
				product.status = rs.getInt(13);
				product.max_stock = rs.getInt(14);
				product.current_stock = rs.getInt(15);
				return product;
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return null;
	}
}
