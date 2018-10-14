package com.opentill.httpServer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.opentill.database.DatabaseHandler;
import com.opentill.document.ChartHelper;
import com.opentill.document.ExcelHelper;
import com.opentill.document.LabelExport;
import com.opentill.document.PDFHelper;
import com.opentill.idata.CustomUser;
import com.opentill.idata.Department;
import com.opentill.idata.Inventory;
import com.opentill.idata.Operators;
import com.opentill.idata.Order;
import com.opentill.idata.Supplier;
import com.opentill.idata.Takings;
import com.opentill.idata.Transaction;
import com.opentill.logging.Log;
import com.opentill.mail.ForgotPasswordEmail;
import com.opentill.mail.MailHandler;
import com.opentill.mail.PasswordResetEmail;
import com.opentill.main.Config;
import com.opentill.main.Utils;
import com.opentill.models.UserAuth;
import com.opentill.products.Product;

import be.ceau.chart.BarChart;
import be.ceau.chart.PieChart;
import be.ceau.chart.color.Color;
import be.ceau.chart.data.BarData;
import be.ceau.chart.data.PieData;
import be.ceau.chart.dataset.BarDataset;
import be.ceau.chart.dataset.PieDataset;
import be.ceau.chart.options.BarOptions;
import be.ceau.chart.options.Legend;
import be.ceau.chart.options.Legend.Position;
import be.ceau.chart.options.PieOptions;

public class API extends AbstractHandler {
	private CustomSessionHandler sessionHandler;

	public API(CustomSessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}

	@SuppressWarnings("unchecked")
	private void getDashboard(ServletRequest baseRequest, ServletResponse response) throws IOException {
		JSONObject jo = new JSONObject();
		jo.put("inventory", Inventory.get_product_levels());
		jo.put("orders", Order.getOrders());
		jo.put("departments", Department.getDepartments());
		jo.put("takings", Takings.getTakings(Utils.getCurrentTimeStamp() - Utils.SECONDS_IN_A_DAY, Utils.getCurrentTimeStamp(), "a10f653a-6c20-11e7-b34e-426562cc935f",
				"PURCHASE"));
		jo.put("suppliers", Supplier.getSuppliers());
		jo.put("operators", Operators.getOperators());
		jo.put("transactions", Transaction.getTransactions((Utils.getCurrentTimeStamp() - (3600 * 24)) / 1000,
				Utils.getCurrentTimeStamp() / 1000));
		response.getWriter().write(jo.toJSONString());
	}

	private void completeOrder(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing id");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblorders SET ended = ? WHERE id = ?"));
			pstmt.setLong(1, Utils.getCurrentTimeStamp());
			pstmt.setString(2, id);
			if (pstmt.executeUpdate() > 0) {
				Log.info("Order ($id) COMPLETED by operator ($operator)");
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void addProductToOrder(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String order = baseRequest.getParameter("order");
		String productId = baseRequest.getParameter("productId");
		String productBarcode = baseRequest.getParameter("productBarcode");
		String quantityString = baseRequest.getParameter("quantity");
		if (order == null || (productId == null && productBarcode == null)) {
			errorOut(response, "missing fields");
			return;
		}
		int quantity;
		if (quantityString == null) {
			quantity = 1;
		} else {
			try {
				quantity = Integer.parseInt(quantityString);
			} catch (NumberFormatException e) {
				errorOut(response, "Could not parse quantity");
				return;
			}
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT INTO :prefix:tblorders_to_products (productId, orderId, quantity, created, updated) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE quantity = quantity + 1"));
			JSONObject productJson;
			if (productBarcode != null) {
				productJson = getItemFromBarcode(productBarcode);
				if (productJson == null) {
					errorOut(response, "Could not find product");
					return;
				}
				pstmt.setString(1, (String) productJson.get("id"));
			} else {
				pstmt.setString(1, productId);
			}
			pstmt.setString(2, order);
			pstmt.setInt(3, quantity);
			pstmt.setLong(4, Utils.getCurrentTimeStamp());
			pstmt.setLong(5, Utils.getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void getOrder(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblproducts.name, "
					+ ":prefix:tblorders_to_products.productId, :prefix:tblorders_to_products.quantity FROM " 
					+ ":prefix:tblorders_to_products LEFT JOIN :prefix:tblproducts ON "
					+ ":prefix:tblproducts.id = :prefix:tblorders_to_products.productId WHERE " 
					+ ":prefix:tblorders_to_products.orderId = ?"));
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				JSONObject tempJo = new JSONObject();
				tempJo.put("name", rs.getString(1));
				tempJo.put("quantity", rs.getInt(3));
				jo.put(rs.getString(2), tempJo);
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("products", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void getOrders(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		JSONObject jo = Order.getOrders();
		if (jo == null) {
			errorOut(response);
			return;
		}
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("success", true);
		responseJSON.put("orders", jo);
		response.getWriter().write(responseJSON.toJSONString());
	}

	private void createOrder(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String supplier = baseRequest.getParameter("supplier");
		if (supplier == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT INTO :prefix:tblorders (id, supplier, created, updated, ended) VALUES (?, ?, ?, ?, 0)"));
			pstmt.setString(1, GUID());
			pstmt.setString(2, supplier);
			pstmt.setLong(3, Utils.getCurrentTimeStamp());
			pstmt.setLong(4, Utils.getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void updateOperator(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		String name = baseRequest.getParameter("name");
		String telephone = baseRequest.getParameter("telephone");
		String email = baseRequest.getParameter("email");
		String password = baseRequest.getParameter("password");
		String comments = baseRequest.getParameter("comments");
		if (id == null || name == null || email == null || password == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT INTO :prefix:operators SET name=?, passwordHash=?, telephone=?, email=?, comments=?, updated=?) WHERE id=?"));
			pstmt.setString(1, name);
			pstmt.setString(2, Utils.hashPassword(password, ""));
			pstmt.setString(3, telephone);
			pstmt.setString(4, email);
			pstmt.setString(5, comments);
			pstmt.setLong(6, Utils.getCurrentTimeStamp());
			pstmt.setString(7, id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void createSupplier(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String name = baseRequest.getParameter("name");
		String telephone = baseRequest.getParameter("telephone");
		String email = baseRequest.getParameter("email");
		String website = baseRequest.getParameter("website");
		String comments = baseRequest.getParameter("comments");
		if (name == null) {
			errorOut(response, "missing params");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblsuppliers (id, name, telephone, email, website, comments, created, updated) VALUES (?,?,?,?,?,?,?,?)"));
			pstmt.setString(1, GUID());
			pstmt.setString(2, name);
			pstmt.setString(3, telephone);
			pstmt.setString(4, email);
			pstmt.setString(5, website);
			pstmt.setString(6, comments);
			pstmt.setLong(7, Utils.getCurrentTimeStamp());
			pstmt.setLong(8, Utils.getCurrentTimeStamp());
			if (pstmt.executeUpdate() > 0) {
				Log.info("Supplier ($id) CREATED by operator ($operator)");
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void updateSupplier(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		String name = baseRequest.getParameter("name");
		String telephone = baseRequest.getParameter("telephone");
		String email = baseRequest.getParameter("email");
		String website = baseRequest.getParameter("website");
		String comments = baseRequest.getParameter("comments");
		if (id == null || name == null) {
			errorOut(response, "missing params");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblsuppliers SET name = ?, telephone = ?, email = ?, website = ?, comments = ?, updated = ? WHERE id = ? LIMIT 1"));
			pstmt.setString(1, name);
			pstmt.setString(2, telephone);
			pstmt.setString(3, email);
			pstmt.setString(4, website);
			pstmt.setString(5, comments);
			pstmt.setLong(6, Utils.getCurrentTimeStamp());
			pstmt.setString(7, id);
			if (pstmt.executeUpdate() > 0) {
				Log.info("Supplier ($id) UPDATED by operator ($operator)");
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);

	}

	private void selectSupplier(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
			return;
		}
		JSONObject jo = Supplier.selectSupplier(id);
		if (jo == null) {
			errorOut(response);
			return;
		}
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("success", true);
		responseJSON.put("supplier", jo);
		response.getWriter().write(responseJSON.toJSONString());
	}

	private void selectOperator(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
			return;
		}
		JSONObject jo = Operators.getOperator(id);
		if (jo == null) {
			errorOut(response);
			return;
		}
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("success", true);
		responseJSON.put("operator", jo);
		response.getWriter().write(responseJSON.toJSONString());
	}

	private void createOperator(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String name = baseRequest.getParameter("name");
		String telephone = baseRequest.getParameter("telephone");
		String email = baseRequest.getParameter("email");
		String password = baseRequest.getParameter("password");
		String comments = baseRequest.getParameter("comments");
		if ((name == null) || (email == null) || (password == null)) {
			errorOut(response, "missing fields");
			return;
		}
		if (Operators.createOperator(name, password, email, telephone, comments)) {
			successOut(response);
		} else {
			errorOut(response);
		}
	}

	private void selectDepartment(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT id, name, shorthand, comments, colour FROM "
					+ ":prefix:tblcatagories WHERE id = ? LIMIT 1"));
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			if (rs.next()) {
				jo.put("id", rs.getString(1));
				jo.put("name", rs.getString(2));
				jo.put("shorthand", rs.getString(3));
				jo.put("comments", rs.getString(4));
				jo.put("colour", rs.getString(5));
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("department", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void deleteDepartment(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblcatagories SET deleted = 1 WHERE id = ? LIMIT 1"));
			pstmt.setString(1, id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);

	}

	private void createDepartment(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String name = baseRequest.getParameter("name");
		String colour = baseRequest.getParameter("colour");
		String shorthand = baseRequest.getParameter("shorthand");
		String comments = baseRequest.getParameter("comments");
		if ((name == null) || (colour == null) || (shorthand == null)) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String guid = GUID();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT INTO :prefix:tblcatagories (id, name, shorthand, colour, comments, orderNum, created, updated) VALUES (?,?,?,?,?,99,UNIX_TIMESTAMP(),UNIX_TIMESTAMP())"));
			pstmt.setString(1, guid);
			pstmt.setString(2, name);
			pstmt.setString(3, shorthand);
			pstmt.setString(4, colour);
			pstmt.setString(5, comments);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.error(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void generateInventoryReport(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String exportType = baseRequest.getParameter("export-type");
		String[] departments = baseRequest.getParameterValues("departments[]");
		if (exportType == null || departments == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblcatagories.id, "
					+ ":prefix:tblproducts.id, :prefix:tblproducts.name, "
					+ ":prefix:tblproducts.current_stock, :prefix:tblproducts.max_stock "
					+ "FROM :prefix:tblproducts LEFT JOIN :prefix:tblcatagories ON "
					+ ":prefix:tblproducts.department = :prefix:tblcatagories.id WHERE "
					+ ":prefix:tblproducts.deleted = 0 ORDER BY :prefix:tblproducts.name DESC"));
			rs = pstmt.executeQuery();
			HashMap<String, HashMap<String, Product>> inventory = new HashMap<String, HashMap<String, Product>>();
			while (rs.next()) {
				Product tempProduct = new Product();
				tempProduct.id = rs.getString(2);
				tempProduct.name = rs.getString(3);
				tempProduct.current_stock = rs.getInt(4);
				tempProduct.max_stock = rs.getInt(5);
				if (inventory.containsKey(rs.getString(1))) {
					HashMap<String, Product> department = inventory.get(rs.getString(1));
					department.put(rs.getString(2), tempProduct);
				} else {
					if (Arrays.asList(departments).indexOf(rs.getString(1)) > -1) {
						HashMap<String, Product> department = new HashMap<String, Product>();
						department.put(rs.getString(2), tempProduct);
						inventory.put(rs.getString(1), department);
					}
				}
			}
			DatabaseHandler.closeDBResources(rs, pstmt, null);
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblcatagories.id, "
					+ ":prefix:tblcatagories.name FROM :prefix:tblcatagories WHERE "
					+ ":prefix:tblcatagories.deleted = 0 ORDER BY :prefix:tblcatagories.name"));
			rs = pstmt.executeQuery();
			HashMap<String, String> departmentsToNames = new HashMap<String, String>();
			while (rs.next()) {
				departmentsToNames.put(rs.getString(1), rs.getString(2));
			}
			File file = new ExcelHelper().createProductLevelsReport(departmentsToNames, departments, inventory);
			if (file == null) {
				errorOut(response);
				return;
			}
			JSONObject responseJo = new JSONObject();
			responseJo.put("success", true);
			responseJo.put("file", Config.OPEN_TILL_URL + file.getName());
			response.getWriter().write(responseJo.toJSONString());
			return;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}

	private void deleteProduct(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing params");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(
					"UPDATE :prefix:tblproducts SET deleted = 1, updated=? WHERE id=?");
			pstmt.setLong(1, Utils.getCurrentTimeStamp());
			pstmt.setString(2, id);
			if (pstmt.executeUpdate() > 0) {
				Log.info("Product ($id) DELETED by operator ($operator)");
				successOut(response);
				return;
			}
			errorOut(response);
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}

	private void generateTakingsReport(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		String exportType = baseRequest.getParameter("takings-export-type");
		String startTimeString = baseRequest.getParameter("start");
		String endTimeString = baseRequest.getParameter("end");
		String[] departments = baseRequest.getParameterValues("departments[]");
		if (exportType == null || startTimeString == null || endTimeString == null || departments == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long startTime = 0L;
		Long endTime = 0L;
		try {
			startTime = Long.parseLong(startTimeString) / 1000;
			endTime = Long.parseLong(endTimeString) / 1000;
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT DATE(FROM_UNIXTIME(:prefix:transactions.ended)) AS \"date\", "
					+ ":prefix:transactiontoproducts.department, SUM(:prefix:transactiontoproducts.price) AS \"amount\" FROM " 
					+ ":prefix:transactiontoproducts LEFT JOIN :prefix:transactions ON :prefix:transactiontoproducts.transaction_id = "
					+ ":prefix:transactions.id WHERE (:prefix:transactions.ended BETWEEN ? AND ?)"
					+ "AND :prefix:transactions.type in (?) GROUP BY :prefix:transactiontoproducts.department, DATE(FROM_UNIXTIME(" 
					+ ":prefix:transactions.ended)) ORDER BY DATE(FROM_UNIXTIME(:prefix:transactions.ended)) DESC"));
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			pstmt.setString(3, "PURCHASE");
			rs = pstmt.executeQuery();
			LinkedHashMap<String, LinkedHashMap<String, Double>> allDates = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
			while (rs.next()) {
				if (allDates.containsKey(rs.getString(1))) {
					LinkedHashMap<String, Double> date = allDates.get(rs.getString(1));
					date.put(rs.getString(2), rs.getDouble(3));
				} else {
					LinkedHashMap<String, Double> date = new LinkedHashMap<String, Double>();
					date.put(rs.getString(2), rs.getDouble(3));
					if (Arrays.asList(departments).indexOf(rs.getString(2)) > -1) {
						allDates.put(rs.getString(1), date);
					}
				}
			}
			pstmt.close();
			rs.close();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblcatagories.id, "
					+ ":prefix:tblcatagories.name FROM :prefix:tblcatagories WHERE "
					+ ":prefix:tblcatagories.deleted = 0 ORDER BY :prefix:tblcatagories.orderNum ASC"));
			rs = pstmt.executeQuery();
			LinkedHashMap<String, String> departmentsToNames = new LinkedHashMap<String, String>();
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
			return;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}

	private void getAllDepartments(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		response.getWriter().write(Department.getDepartmentsWithInfo().toJSONString());
	}

	private void getAllOperators(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		JSONArray jo = Operators.getOperators();
		if (jo == null) {
			errorOut(response);
			return;
		}
		response.getWriter().write(jo.toJSONString());
	}

	private void getAllSuppliers(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		JSONArray joArray = Supplier.getSuppliers();
		response.getWriter().write(joArray.toJSONString());
	}

	private void sendMessage(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String sender = baseRequest.getParameter("from");
		String message = baseRequest.getParameter("message");
		String recipient = baseRequest.getParameter("to");
		if ((sender == null) || (message == null)) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT :prefix:tblchat (id, sender, recipient, message, updated, created) VALUES (?, ?, ?, ?, ?, ?)"));
			pstmt.setString(1, GUID());
			pstmt.setString(2, sender);
			pstmt.setString(3, recipient);
			pstmt.setString(4, message);
			pstmt.setLong(5, Utils.getCurrentTimeStamp());
			pstmt.setLong(6, Utils.getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void printLabel(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing parameters");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblproducts SET labelprinted = 1 WHERE id = ?"));
			pstmt.setString(1, id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void getProductsLevels(ServletRequest baseRequest, ServletResponse response) throws IOException {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("success", true);
		responseJSON.put("products", Inventory.get_product_levels());
		response.getWriter().write(responseJSON.toJSONString());
	}

	private void setCurrentStockLevel(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		String amountString = baseRequest.getParameter("amount");
		if (id == null || amountString == null) {
			errorOut(response, "missing fields");
		}
		int amount = Integer.parseInt(amountString);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(
					"UPDATE :prefix:tblproducts SET current_stock=? WHERE id = ?");
			pstmt.setString(1, id);
			pstmt.setInt(1, amount);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
			errorOut(response);
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}

	private void setMaxStockLevel(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		String id = baseRequest.getParameter("id");
		String amountString = baseRequest.getParameter("amount");
		if (id == null || amountString == null) {
			errorOut(response, "missing fields");
		}
		int amount = Integer.parseInt(amountString);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(
					"UPDATE :prefix:tblproducts SET max_stock=? WHERE id = ?");
			pstmt.setString(1, id);
			pstmt.setInt(1, amount);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
			errorOut(response);
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}

	private void getProduct(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
		}
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblproducts.id, "
					+ ":prefix:tblproducts.name, :prefix:tblproducts.price, :prefix:tblproducts.barcode, "
					+ ":prefix:tblproducts.department, :prefix:tblproducts.max_stock, :prefix:tblproducts.current_stock "
					+ "FROM :prefix:tblproducts WHERE id = ? LIMIT 1"));
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject product = new JSONObject();
			if (rs.next()) {
				product.put("id", rs.getString(1));
				product.put("name", rs.getString(2));
				product.put("price", rs.getString(3));
				product.put("barcode", rs.getString(4));
				product.put("department", rs.getString(5));
				product.put("max_stock", rs.getString(6));
				product.put("current_stock", rs.getString(7));
			}
			JSONObject jo = new JSONObject();
			jo.put("success", true);
			jo.put("product", product);
			response.getWriter().write(jo.toJSONString());
			return;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}

	private void getTakings(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		String type = baseRequest.getParameter("type") == null ? "PURCHASE" : baseRequest.getParameter("type");
		if (startString == null || endString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long start = 0L;
		Long end = 0L;
		try {
			start = Long.parseLong(startString);
			end = Long.parseLong(endString);
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse quantity");
			return;
		}
		JSONObject jo = Takings.getTakings(start, end, admin, type);
		JSONObject responseJo = new JSONObject();
		responseJo.put("success", true);
		responseJo.put("totals", jo);
		response.getWriter().write(jo.toJSONString());
		return;
	}

	@SuppressWarnings("unchecked")
	private void search(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String search = baseRequest.getParameter("search");
		if (search.length() == 0) {
			errorOut(response, "No search criteria");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblproducts.id, :prefix:tblproducts.name, :prefix:tblproducts.barcode, "
							+ ":prefix:tblproducts.price FROM :prefix:tblproducts WHERE "
							+ "LOWER(name) LIKE LOWER(?) AND deleted = 0 ORDER BY name LIMIT 20"));
			pstmt.setString(1, "%" + search + "%");
			rs = pstmt.executeQuery();
			JSONObject responseJson = new JSONObject();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("name", rs.getString(2));
				jo.put("barcode", rs.getString(3));
				jo.put("price", rs.getDouble(4));
				responseJson.put(rs.getString(1), jo);
			}
			response.getWriter().write(responseJson.toJSONString());
			return;
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}

	private void updateProduct(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing id");
			return;
		}
		String name = baseRequest.getParameter("name");
		String priceText = baseRequest.getParameter("price");
		String department = baseRequest.getParameter("department");
		String currentStockString = baseRequest.getParameter("current_stock");
		String maxStockString = baseRequest.getParameter("max_stock");
		boolean autoPricing = baseRequest.getParameter("autoPricing") == "true" ? true : false;
		String supplierPriceString = baseRequest.getParameter("supplierPrice");
		String unitsInCaseString = baseRequest.getParameter("unitsInCase");
		boolean includesVAT = baseRequest.getParameter("includesVAT") == "true" ? true : false;
		String VATamountString = baseRequest.getParameter("VATamount");
		boolean targetPercentage = baseRequest.getParameter("targetPercentage") == "true" ? true : false;;
		String targetProfitMarginString = baseRequest.getParameter("targetProfitMargin");
		if (name == null || priceText == null || department == null) {
			errorOut(response, "missing fields");
			return;
		}
		float price;
		int currentStockInt = 0;
		int maxStockInt = 0;
		float supplierPrice = 0.0F;
		int unitsInCase = 0;
		float VATamount = 0.0F;
		float targetProfitMargin = 0.0F;
		try {
			price = Float.parseFloat(priceText);
			currentStockInt = Integer.parseInt(currentStockString);
			maxStockInt = Integer.parseInt(maxStockString);
			supplierPrice = Float.parseFloat(supplierPriceString);
			unitsInCase = Integer.parseInt(unitsInCaseString);
			VATamount = Float.parseFloat(VATamountString);
			targetProfitMargin = Float.parseFloat(targetProfitMarginString);
		} catch (NumberFormatException ex) {
			Log.info(ex.getMessage());
			errorOut(response, "Could not interpret numeric field");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblproducts SET name = ?, price = ?, department=?, current_stock=?, max_stock=?, supplierPrice = ?, unitsInCase = ?, VATamount=?,  targetProfitMargin = ?, updated = UNIX_TIMESTAMP() WHERE id=?"));
			pstmt.setString(1, name);
			pstmt.setFloat(2, price);
			pstmt.setString(3, department);
			pstmt.setInt(4, currentStockInt);
			pstmt.setInt(5, maxStockInt);
			pstmt.setFloat(6, supplierPrice);
			pstmt.setInt(7, unitsInCase);
			pstmt.setFloat(8, VATamount);
			pstmt.setFloat(9, targetProfitMargin);
			pstmt.setString(10, id);
			if (pstmt.executeUpdate() > 0) {
				Log.info("Product ($id) UPDATED by operator ($operator)");
				successOut(response);
				return;
			}
			errorOut(response);
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}

	private void cancelTransaction(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		String id = baseRequest.getParameter("transaction_id");
		if (!transactionExists(id)) {
			errorOut(response, "transaction does not exist");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("DELETE FROM :prefix:transactions WHERE (id = ? AND ended = 0) LIMIT 1"));
			pstmt.setString(1, id);
			if (pstmt.executeUpdate() > 0) {
				successOut(response);
				return;
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}

	private void completeTransaction(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		String id = baseRequest.getParameter("id");
		float money_given = (float) (baseRequest.getParameter("money_given") == null ? 0.00
				: Float.parseFloat(baseRequest.getParameter("money_given")));
		float total = (float) (baseRequest.getParameter("total") == null ? 0.00
				: Float.parseFloat(baseRequest.getParameter("total")));
		float card = (float) (baseRequest.getParameter("card") == null ? 0.00
				: Float.parseFloat(baseRequest.getParameter("card")));
		float cashback = (float) (baseRequest.getParameter("cashback") == null ? 0.00
				: Float.parseFloat(baseRequest.getParameter("cashback")));
		String type = baseRequest.getParameter("type");
		String payee = baseRequest.getParameter("payee");
		String cashier_id = baseRequest.getParameter("cashier");
		String json = baseRequest.getParameter("json");
		if (id == null) {
			errorOut(response);
			return;
		}
		if (!transactionExists(id)) {
			errorOut(response, "transaction does not exist");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if (!writeTransactionFileToDatabase(id, json, type)) {
				Log.info("Failed to write transaction(" + id + ") to database, rolling back");
				errorOut(response);
				return;
			}
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:transactions SET ended = ?, total = ?, cashback=?, money_given = ?, card = ?, type=?, payee=? WHERE id=? AND cashier=?"));
			pstmt.setLong(1, Utils.getCurrentTimeStamp() / 1000);
			pstmt.setFloat(2, total);
			pstmt.setFloat(3, cashback);
			pstmt.setFloat(4, money_given);
			pstmt.setFloat(5, card);
			pstmt.setString(6, type);
			pstmt.setString(7, payee);
			pstmt.setString(8, id);
			pstmt.setString(9, cashier_id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				Log.info("Transaction " + id + " COMPLETED by operator (" + cashier_id + ")");
				successOut(response);
				return;
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}

	private boolean writeTransactionFileToDatabase(String id, String json, String type) throws SQLException {
		
		if (json == null) {
			return false;
		}
		JSONObject jo = null;
		try {
			JSONParser parser = new JSONParser();
			jo = (JSONObject) parser.parse(json);
		} catch (ParseException ex) {
			Log.info(ex.getMessage());
			return false;
		}
		Connection conn = null;
		try {
			conn = DatabaseHandler.getDatabase();
			conn.setAutoCommit(false);
			JSONArray products = (JSONArray) jo.get("products");
			for (int i = 0; i < products.size(); i++) {
				JSONObject joProduct = (JSONObject) products.get(i);
				Long productQuantity = (Long) joProduct.get("quantity");
				if (productQuantity == 0L) {
					Log.info("Error, transaction (" + id + ") tried to buy 0 products (" + joProduct.get("id") + ")");
					continue;
				}
				switch (type) {
				case "PURCHASE":
					decrementProductLevel(conn, id, productQuantity);
					break;
				case "REFUND":
					incrementProductLevel(conn, id, productQuantity);
					break;
				}
				// TODO:: USE A DIFFERENT LIBRARY TO HANDLE THE TRANSACTION
				while (productQuantity-- > 0) {
					if (!insertTransactionProduct(conn, id,
							((boolean) joProduct.get("inDatabase") ? (String) joProduct.get("id") : null),
							(double) joProduct.get("price"), (String) joProduct.get("department"))) {
						throw new Exception();
					}
				}
			}
			conn.commit();
			return true;
		} catch (Exception ex) {
			conn.rollback();
			Log.info(ex.toString());
			return false;
		} finally {
			DatabaseHandler.closeDBResources(null, null, conn);
		}
	}

	private boolean insertTransactionProduct(Connection conn, String transactionId, String productId, double price,
			String departmentId) throws SQLException {
		String noneCatagory = "5b82f89a-7b71-11e7-b34e-426562cc935f";
		PreparedStatement pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT INTO :prefix:transactiontoproducts (id, transaction_id, product_id, price, department, created) VALUES (?, ?, ?, ?, ?, ?)"));
		pstmt.setString(1, GUID());
		pstmt.setString(2, transactionId);
		pstmt.setString(3, productId);
		pstmt.setDouble(4, price);
		pstmt.setString(5, departmentId == null ? noneCatagory : departmentId);
		pstmt.setLong(6, Utils.getCurrentTimeStamp() / 1000);
		pstmt.execute();
		if (pstmt.getUpdateCount() > 0) {
			return true;
		}
		return false;
	}

	private boolean incrementProductLevel(Connection conn, String productId, Long productQuantity) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblproducts SET current_stock=current_stock+? WHERE id = ?"));
		pstmt.setLong(1, productQuantity);
		pstmt.setString(2, productId);
		if (pstmt.executeUpdate() > 0) {
			return true;
		}
		return false;
	}

	private boolean decrementProductLevel(Connection conn, String productId, Long productQuantity) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:tblproducts SET current_stock=current_stock-? WHERE id = ? AND current_stock > 0"));
		pstmt.setLong(1, productQuantity);
		pstmt.setString(2, productId);
		if (pstmt.executeUpdate() > 0) {
			return true;
		}
		return false;
	}

	private boolean transactionExists(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT 1 FROM :prefix:transactions WHERE (id = ? AND ended = 0) LIMIT 1"));
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void getDayTotals(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		JSONObject jo = new JSONObject();
		jo.put("success", true);
		jo.put("card", cardGiven(baseRequest, response));
		jo.put("cashback", cashback(baseRequest, response));
		jo.put("takings", takings(baseRequest, response));
		jo.put("refunds", refunds(baseRequest, response));
		jo.put("payouts", payouts(baseRequest, response));
		response.getWriter().write(jo.toJSONString());
	}

	private BigDecimal payouts(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			errorOut(response, "missing fields");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT IFNULL(SUM("				
					+ ":prefix:transactions.total), 0.00) AS \"amount\" FROM "
					+ ":prefix:transactions WHERE (:prefix:transactions.started > ? AND "
					+ ":prefix:transactions.ended < ?) AND :prefix:transactions.cashier NOT IN (?) AND "
					+ ":prefix:transactions.type in (?) AND (:prefix:transactions.ended > 0)"));
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PAYOUT");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBigDecimal(1);
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return new BigDecimal(0.00);
	}

	private BigDecimal refunds(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			errorOut(response, "missing fields");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT SUM(:prefix:transactiontoproducts.price) AS \"amount\" FROM " 
					+ ":prefix:transactiontoproducts LEFT JOIN :prefix:transactions ON "
					+ ":prefix:transactions.id = :prefix:transactiontoproducts.transaction_id WHERE ("
					+ ":prefix:transactions.started > ? AND :prefix:transactions.ended < ?) AND "
					+ ":prefix:transactions.cashier NOT IN (?) AND "
					+ ":prefix:transactions.type in (?) AND ("
					+ ":prefix:transactions.ended > 0) GROUP BY :prefix:transactiontoproducts.department"));
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "REFUND");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBigDecimal(1);
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return new BigDecimal(0.00);
	}

	private BigDecimal takings(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			errorOut(response, "missing fields");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT SUM(:prefix:transactions.total) AS \"amount\" FROM :prefix:transactions WHERE ("
					+ ":prefix:transactions.started > ? AND :prefix:transactions.ended < ?) AND :prefix:transactions.cashier NOT IN (?) AND " 
					+ ":prefix:transactions.type in (?) AND (:prefix:transactions.ended > 0)"));
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBigDecimal(1);
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return new BigDecimal(0.00);
	}

	private BigDecimal cashback(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			throw new ServletException("Missing Parameters");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT SUM(:prefix:transactions.cashback) AS \"amount\" FROM :prefix:transactions WHERE (:prefix:transactions.started > ? AND "
					+ ":prefix:transactions.ended < ?) AND :prefix:transactions.cashier NOT IN (?) AND :prefix:transactions.type in (?) AND (:prefix:transactions.ended > 0)"));
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return new BigDecimal(rs.getDouble(1));
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return new BigDecimal(0.00);
	}

	private BigDecimal cardGiven(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			throw new ServletException("Missing parameters");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT SUM(:prefix:transactions.card) AS \"amount\" FROM :prefix:transactions WHERE ("
					+ ":prefix:transactions.started > ? AND :prefix:transactions.ended < ?) AND :prefix:transactions.cashier NOT IN (?) "
					+ "AND :prefix:transactions.type in (?) AND (:prefix:transactions.ended > 0)"));
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return new BigDecimal(rs.getDouble(1));
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return new BigDecimal(0.00);
	}

	@SuppressWarnings("unchecked")
	private void getTransactions(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long start = 0L;
		Long end = 0L;
		try {
			start = Long.parseLong(startString);
			end = Long.parseLong(endString);
		} catch (NumberFormatException ex) {
			errorOut(response, "Could not parse start or end");
			return;
		}
		response.getWriter().write(Transaction.getTransactions(start, end).toJSONString());
	}

	private void getProductTransactions(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String id = baseRequest.getParameter("id");
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (id == null || startString == null || endString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		long start = 0;
		long end = 0;
		try {
			start = Long.parseLong(startString);
			end = Long.parseLong(endString);
		} catch (NumberFormatException ex) {
			errorOut(response, "Could not parse start or end");
			return;
		}
		try {
			JSONArray jsonArr = new JSONArray();
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT * FROM :prefix:transactiontoproducts WHERE product_id = ? AND created BETWEEN ? AND ?"));
			pstmt.setString(1, id);
			pstmt.setLong(2, start);
			pstmt.setLong(3, end);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs.getString(1));
				jsonObject.put("quantity", rs.getString(2));
				jsonObject.put("price", rs.getString(3));
				jsonObject.put("name", rs.getString(4));
				jsonArr.add(jsonObject);
				return;
			}
			response.getWriter().write(jsonArr.toJSONString());
		} catch (SQLException ex) {
			Log.error(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}

	@SuppressWarnings("unchecked")
	private void getTransactionProducts(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		JSONArray jsonArr = new JSONArray();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing parameters");
			return;
		}
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:transactiontoproducts.product_id, COUNT(*) AS \"quantity\", :prefix:transactiontoproducts.price, IFNULL((SELECT name FROM "
					+ ":prefix:tblproducts WHERE id = :prefix:transactiontoproducts.product_id LIMIT 1), :prefix:tblcatagories.name) AS \"name\" FROM "
					+ ":prefix:transactiontoproducts LEFT JOIN :prefix:tblproducts ON :prefix:tblproducts.id = :prefix:transactiontoproducts.product_id LEFT JOIN :prefix:tblcatagories ON "
					+ ":prefix:tblcatagories.id = :prefix:transactiontoproducts.department WHERE :prefix:transactiontoproducts.transaction_id = ? GROUP BY "
					+ ":prefix:transactiontoproducts.product_id, :prefix:transactiontoproducts.price, :prefix:tblcatagories.name"));
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs.getString(1));
				jsonObject.put("quantity", rs.getString(2));
				jsonObject.put("price", rs.getString(3));
				jsonObject.put("name", rs.getString(4));
				jsonArr.add(jsonObject);
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
			errorOut(response);
			return;
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		response.getWriter().write(jsonArr.toJSONString());
	}

	private void logout(HttpServletRequest request, ServletResponse response) throws IOException, ServletException {
		String session = this.getAuthCookieValue(request);
		if (session == null) {
			errorOut(response, "missing parameters");
			return;
		}
		sessionHandler.destroySession(session);
		successOut(response);
	}

	private void login(Request baseRequest, ServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String data = Utils.getJSONFromRequest(baseRequest);
		UserAuth userAuth = new Gson().fromJson(data, UserAuth.class);
		if (!userAuth.isValid()) {
			errorOut(response, "missing parameters");
			return;
		}
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT id, name, type, email, telephone FROM :prefix:operators WHERE LCASE(email) = LCASE(?) AND passwordHash = ? LIMIT 1"));
			pstmt.setString(1, userAuth.getEmail());
			pstmt.setString(2, userAuth.getHashedPassword());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				CustomUser user = new CustomUser();
				user.setId(rs.getString(1));
				user.setName(rs.getString(2));
				user.setType(rs.getInt(3));
				user.setEmail(rs.getString(4));
				user.setTelephone(rs.getString(5));
				Log.info("User (" + user.getName() + ") logged in successfully");
				Cookie cookie = new Cookie(Config.AUTH_COOKIE_NAME, sessionHandler.createUserSession(user));
				cookie.setPath("/");
				response.addCookie(cookie);
				successOut(response);
				return;
			}
			errorOut(response, "Incorrect Email/Password combination");
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}

	private void startTransaction(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String operator = baseRequest.getParameter("cashier_id");
		if (operator == null) {
			errorOut(response, "missing fields");
			return;
		}
		String guid = Transaction.startTransaction(operator);
		if (guid == null) {
			errorOut(response, "could not start transaction");
			return;
		}
		JSONObject responseJo = new JSONObject();
		responseJo.put("success", true);
		responseJo.put("id", guid);
		response.getWriter().write(responseJo.toJSONString());

	}

	private void getMessages(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String operator = baseRequest.getParameter("operator");
		String timeString = baseRequest.getParameter("time");
		if (operator == null || timeString == null) {
			errorOut(response, "missing fields");
			return;
		}
		int time = 0;
		try {
			time = Integer.parseInt(timeString);
		} catch (NumberFormatException e) {
			Log.info(e.toString());
			errorOut(response, "time not formatted correctly");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblchat.id, :prefix:tblchat.sender AS \"senderId\", :prefix:tblchat.recipient as \"recipientId\", a.name AS \"senderName\", IFNULL(b.name, \"All\") AS recipientName, "
					+ ":prefix:tblchat.message, :prefix:tblchat.created FROM :prefix:tblchat LEFT JOIN :prefix:operators a ON a.id = :prefix:tblchat.sender LEFT JOIN :prefix:operators b ON b.id = "
					+ ":prefix:tblchat.recipient WHERE (:prefix:tblchat.recipient = ? OR :prefix:tblchat.sender = ? OR :prefix:tblchat.recipient = \"All\") AND "
					+ ":prefix:tblchat.created > ? ORDER BY :prefix:tblchat.created ASC"));
			pstmt.setString(1, operator);
			pstmt.setString(2, operator);
			pstmt.setInt(3, time);
			rs = pstmt.executeQuery();
			JSONArray messages = new JSONArray();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getString(1));
				jo.put("senderId", rs.getString(2));
				jo.put("recipientId", rs.getString(3));
				jo.put("senderName", rs.getString(4));
				jo.put("recipientName", rs.getString(5));
				jo.put("message", rs.getString(6));
				jo.put("created", rs.getInt(7));
				messages.add(jo);
			}
			JSONObject responseJson = new JSONObject();
			responseJson.put("success", true);
			responseJson.put("messages", messages);
			response.getWriter().write(responseJson.toJSONString());
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getCaseFromBarcode(String barcode) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblcases.id, :prefix:tblcases.product, :prefix:tblcases.units"
					+ "WHERE barcode = ? LIMIT 1"));
			pstmt.setString(1, barcode);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs.getString(1));
				jsonObject.put("product", rs.getString(2));
				jsonObject.put("units", rs.getInt(3));
				jsonObject.put("barcode", barcode);
				jsonObject.put("isCase", true);
				return jsonObject;
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getItemFromBarcode(String barcode) {
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
					+ ":prefix:tblproducts.current_stock FROM :prefix:tblproducts WHERE barcode = ? LIMIT 1"));
			pstmt.setString(1, barcode);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs.getString(1));
				jsonObject.put("name", rs.getString(2));
				jsonObject.put("price", rs.getString(3));
				jsonObject.put("barcode", rs.getString(4));
				jsonObject.put("department", rs.getString(5));
				jsonObject.put("labelprinted", rs.getInt(6));
				jsonObject.put("isCase", rs.getInt(7));
				jsonObject.put("units", rs.getString(8));
				jsonObject.put("unitType", rs.getString(9));
				jsonObject.put("updated", rs.getInt(10));
				jsonObject.put("created", rs.getInt(11));
				jsonObject.put("deleted", rs.getInt(12));
				jsonObject.put("status", rs.getInt(13));
				jsonObject.put("max_stock", rs.getInt(14));
				jsonObject.put("current_stock", rs.getInt(15));
				return jsonObject;
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return null;
	}

	public String GUID() {
		return UUID.randomUUID().toString();
	}

	public String insertProduct(String barcode, String name) {
		String guid = GUID();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT IGNORE INTO :prefix:tblproducts (id, barcode, name, created) VALUES (?, ?, ?, ?)"));
			pstmt.setString(1, guid);
			pstmt.setString(2, barcode);
			pstmt.setString(3, name);
			pstmt.setLong(4, new Timestamp(System.currentTimeMillis()).getTime());
			if (pstmt.executeUpdate() > 0) {
				return guid;
			}
			return null;
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
	
	public boolean insertCase(String barcode, String product, int units) {
		String guid = GUID();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("INSERT IGNORE INTO :prefix:tblcases (id, barcode, product, units, created) VALUES (?, ?, ?, ?, ?)"));
			pstmt.setString(1, guid);
			pstmt.setString(2, barcode);
			pstmt.setString(3, product);
			pstmt.setInt(4, units);
			pstmt.setLong(5, Utils.getCurrentTimeStamp());
			if (pstmt.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return false;
	}

	public void operatorLogin(ServletRequest baseRequest, ServletResponse response)
			throws IOException, ServletException {
		String code = baseRequest.getParameter("code");
		JSONObject json = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		if (code == null) {
			errorOut(response, "missing fields");
			return;
		}
		Log.info(code + "attemping to login");
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:operators.id, :prefix:operators.name FROM :prefix:operators WHERE code = ? AND deleted = 0 LIMIT 1"));
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				json = new JSONObject();
				json.put("success", true);
				json.put("id", rs.getString(1));
				json.put("name", rs.getString(2));
				response.getWriter().write(json.toJSONString());
				Log.info("Operator " + rs.getString(2) + "(" + rs.getString(1) + ") logged in successfully");
				return;
			}
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response, null);
	}

	private void getTopProductSales(HttpServletRequest baseRequest, HttpServletResponse response) throws IOException, ServletException {
		JSONArray json = new JSONArray();
		String time_interval = baseRequest.getParameter("time_interval") != null ? baseRequest.getParameter("time_interval") : "HOUR";
		String startTimeString = baseRequest.getParameter("start");
		String endTimeString = baseRequest.getParameter("end");
		if (startTimeString == null || endTimeString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long startTime = 0L;
		Long endTime = 0L;
		try {
			startTime = Long.parseLong(startTimeString);
			endTime = Long.parseLong(endTimeString);
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:transactiontoproducts.product_id, :prefix:tblproducts.name,  "
					+ "COUNT(:prefix:transactiontoproducts.product_id) AS \"Sales\" FROM :prefix:transactiontoproducts INNER JOIN "
					+ ":prefix:tblproducts ON :prefix:tblproducts.id = :prefix:transactiontoproducts.product_id WHERE "
					+ ":prefix:transactiontoproducts.created BETWEEN ? AND ? GROUP BY "
					+ ":prefix:transactiontoproducts.product_id ORDER BY COUNT(:prefix:transactiontoproducts.product_id) DESC LIMIT 10"));
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject tempJo = new JSONObject();
				tempJo.put("id", rs.getString(1));
				tempJo.put("name", rs.getString(2));
				tempJo.put("value", rs.getInt(3));
				json.add(tempJo);
			}
			response.getWriter().write(json.toJSONString());
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}

	public void barcode(ServletRequest baseRequest, ServletResponse response) throws IOException, ServletException {
		String barcode = baseRequest.getParameter("number");
		boolean isCase = baseRequest.getParameter("isCase") != null ? (baseRequest.getParameter("isCase") == "true" ? true : false) : false;
		JSONObject product = null;
		if (barcode == null) {
			errorOut(response, "no barcode");
			return;
		}
		if (!barcode.matches("-?\\d+(\\.\\d+)?")) { // is numeric
			errorOut(response, "invalid barcode");
			return;
		}
		product = getItemFromBarcode(barcode);
		if (product != null) {
			response.getWriter().write(product.toJSONString());
			return;
		}
		product = getCaseFromBarcode(barcode);
		if (product != null) {
			response.getWriter().write(product.toJSONString());
			return;
		}
		if (isCase) {
			JSONObject jo = new JSONObject();
			jo.put("success", false);
			response.getWriter().write(jo.toJSONString());
			return;
		}
		insertProduct(barcode, "Unknown Product");
		product = new JSONObject();
		product = getItemFromBarcode(barcode);
		product.put("isNew", true);
		response.getWriter().write(product.toJSONString());
	}

	@SuppressWarnings("unchecked")
	public void getDepartments(ServletResponse response) throws IOException, ServletException {
		response.getWriter().write(Department.getDepartmentsWithInfo().toJSONString());
	}

	public void errorOut(ServletResponse response) throws IOException, ServletException {
		errorOut(response, null);
	}

	@SuppressWarnings("unchecked")
	public void errorOut(ServletResponse response, String reason) throws IOException, ServletException {
		JSONObject json = new JSONObject();
		json.put("success", false);
		if (reason != null) {
			json.put("reason", reason);
		}
		response.getWriter().write(json.toJSONString());
	}

	@SuppressWarnings("unchecked")
	public void successOut(ServletResponse response) throws IOException, ServletException {
		JSONObject json = new JSONObject();
		json.put("success", true);
		response.getWriter().write(json.toJSONString());
	}
	
	public String getCookieValue(HttpServletRequest request, String cookieName) {
		String value = null;
		if ((request.getCookies() != null) && (20 > request.getCookies().length) && (request.getCookies().length > 0)) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(cookieName)) {
					value = cookie.getValue();
				}
			}
		}
		return value;
	}
	
	public String getAuthCookieValue(HttpServletRequest request) {
		return getCookieValue(request, "auth");
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		if (request.getParameter("function") == null) {
			errorOut(response, "No such function");
		}
		switch (request.getParameter("function")) {
		case "BARCODE":
			barcode(request, response);
			break;
		case "TRANSACTION":
			startTransaction(request, response);
			break;
		case "DEPARTMENTS":
			getDepartments(response);
			break;
		case "DASHBOARD":
			getDashboard(request, response);
			break;
		case "LOGIN":
			login(baseRequest, request, response);
			break;
		case "FORGOTPASSWORD":
			forgotPassword(request, response);
			break;
		case "LOGOUT":
			logout(request, response);
			break;
		case "GETTRANSACTION":
			getTransactionProducts(request, response);
			break;
		case "GETPRODUCTSALES":
			getProductTransactions(request, response);
			break;
		case "GETTRANSACTIONS":
			getTransactions(request, response);
			break;
		case "GETDAYTOTALS":
			getDayTotals(request, response);
			break;
		case "COMPLETETRANSACTION":
			completeTransaction(request, response);
			break;
		case "CLEARTRANSACTION":
			cancelTransaction(request, response);
			break;
		case "UPDATEPRODUCT":
			updateProduct(request, response);
			break;
		case "GETPRODUCT":
			getProduct(request, response);
			break;
		case "PRINTLABEL":
			printLabel(request, response);
			break;
		case "GETALLSUPPLIERS":
			getAllSuppliers(request, response);
			break;
		case "GENERATETAKINGSREPORT":
			generateTakingsReport(request, response);
			break;
		case "GENERATEINVENTORYREPORT":
			generateInventoryReport(request, response);
			break;
		case "GETSUPPLIER":
			selectSupplier(request, response);
			break;
		case "UPDATESUPPLIER":
			updateSupplier(request, response);
			break;
		case "ADDSUPPLIER":
			createSupplier(request, response);
			break;
		case "DELETESUPPLIER":
			// deleteSupplier(request, response);
			break;
		case "GETALLOPERATORS":
			getAllOperators(request, response);
			break;
		case "GETOPERATOR":
			selectOperator(request, response);
			break;
		case "UPDATEOPERATOR":
			updateOperator(request, response);
			break;
		case "ADDOPERATOR":
			createOperator(request, response);
			break;
		case "DELETEOPERATOR":
			// deleteOperator(request, response);
			break;
		case "GETALLDEPARTMENTS":
			getAllDepartments(request, response);
			break;
		case "GETDEPARTMENT":
			selectDepartment(request, response);
			break;
		case "UPDATEDEPARTMENT":
			// updateDepartment(request, response);
			break;
		case "ADDDEPARTMENT":
			createDepartment(request, response);
			break;
		case "DELETEDEPARTMENT":
			deleteDepartment(request, response);
			break;
		case "DELETEPRODUCT":
			deleteProduct(request, response);
			break;
		case "SEARCH":
			search(request, response);
			break;
		case "TAKINGS":
			getTakings(request, response);
			break;
		case "GETLABELS":
			getLabels(request, response);
			break;
		case "CLEARLABELS":
			// clearLabels(request, response);
			break;
		case "OPERATORLOGON":
			operatorLogin(request, response);
			break;
		case "TOTALS":
			getTakings(request, response);
			break;
		case "SENDMESSAGE":
			sendMessage(request, response);
			break;
		case "GETMESSAGES":
			getMessages(request, response);
			break;
		case "SAVELABELSTYLE":
			// saveLabel(request, response);
			break;
		case "GETLABELSTYLE":
			// getLabelStyle(request, response);
			break;
		case "GETLABELSTYLES":
			// getLabelStyles(request, response);
			break;
		case "ISLOGGEDIN":
			// isLoggedIn(request, response);
			break;
		case "GETPRODUCTLEVELS":
			// getProductLevels(request, response);
			break;
		case "GETPRODUCTSLEVELS":
			getProductsLevels(request, response);
			break;
		case "CHANGEMAXSTOCKLEVEL":
			setMaxStockLevel(request, response);
			break;
		case "CHANGECURRENTSTOCKLEVEL":
			setCurrentStockLevel(request, response);
			break;
		case "CREATEORDER":
			createOrder(request, response);
			break;
		case "COMPLETEORDER":
			completeOrder(request, response);
			break;
		case "GETORDER":
			getOrder(request, response);
			break;
		case "GETORDERS":
			getOrders(request, response);
			break;
		case "ADDPRODUCTTOORDER":
			addProductToOrder(request, response);
			break;
		case "TOPPRODUCTSALES":
			getTopProductSales(request, response);
			break;
		case "GETTAKINGSCHART":
			getTakingsChart(request, response);
			break;
		case "GETUSERINFO":
			getUserInfo(request, response);
			break;
		case "GENERATELABELSPDF":
			generateLabelsPDF(request, response);
			break;
		case "GENERATETAKINGSGRAPH":
			generateTakingsGraph(request, response);
			break;
		case "GETOPERATORTOTALS":
			getOperatorTotals(request, response);
			break;
		case "GETTAKINGSBYDEPARTMENT":
			getTakingsByDepartment(request, response);
			break;
		case "GETOVERVIEWTOTALS":
			getOverviewTotals(request, response);
			break;
		case "GENERATELBXLABEL":
			generateLbxLabel(request, response);
			break;
		case "RESETPASSWORD":
			resetPassword(request, response);
			break;
		default:
			errorOut(response, "No such function");
			break;
		}
		response.getWriter().flush();
		response.getWriter().close();

	}

	private void resetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String newPassword = request.getParameter("newPassword");
		String id = request.getParameter("id");
		String token = request.getParameter("token");
		if (Utils.anyNulls(newPassword, id, token)) {
			errorOut(response, "missing parameters");
			return;
		}
		try {
			String userId = Operators.getUserIdFromResetPasswordLink(id, token);
			if (userId == null) {
				errorOut(response, "Password Reset Link Expired");
				return;
			}
			JSONObject operator = Operators.getOperator(userId);
			if (operator == null) {
				errorOut(response, "operator does not exist");
				return;
			}
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("UPDATE :prefix:operators SET passwordHash=? WHERE id=?"));
			pstmt.setString(1, Utils.hashPassword(newPassword, ""));
			pstmt.setString(2,  userId);
			if (pstmt.executeUpdate() > 0) {
				org.simplejavamail.email.Email msg = new PasswordResetEmail(operator.get("name").toString(), operator.get("email").toString()).toEmail();
				MailHandler.add(msg);
				Log.info("User (" + userId + ") requested password reset");
				successOut(response);
				return;
			}
			errorOut(response, "No user found");
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response, "Unknown Error");
	}

	private void forgotPassword(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String email = request.getParameter("email");
		if (email == null) {
			errorOut(response, "missing parameters");
			return;
		}
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT id, name FROM :prefix:operators WHERE LCASE(email) = LCASE(?) LIMIT 1"));
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String passwordLink = Operators.createPasswordResetLink(rs.getString(1));
				org.simplejavamail.email.Email msg = new ForgotPasswordEmail(rs.getString(1), rs.getString(2), email, passwordLink).toEmail();
				MailHandler.add(msg);
				Log.info("User (" + rs.getString(2) + ") requested password reset");
				successOut(response);
				return;
			}
			errorOut(response, "No user found");
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} catch (Exception ex) {
			Log.info(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response, "Unknown Error");
	}

	private void generateLbxLabel(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String id = request.getParameter("id");
		if (id == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Product product = Product.getProduct(id);
		if (product == null) {
			errorOut(response, "product not found");
			return;
		}
		File file = LabelExport.generateLabel(product);
		JSONObject responseJo = new JSONObject();
		responseJo.put("success", true);
		responseJo.put("file", String.format("%s/temp/%s", Config.OPEN_TILL_URL, file.getName()));
		response.getWriter().write(responseJo.toJSONString());
	}

	private void getOverviewTotals(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String startTimeString = request.getParameter("start");
		String endTimeString = request.getParameter("end");
		if (startTimeString == null || endTimeString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long startTime = 0L;
		Long endTime = 0L;
		try {
			startTime = Long.parseLong(startTimeString);
			endTime = Long.parseLong(endTimeString);
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT SUM(:prefix:transactions.total) AS 'revenue', COUNT(:prefix:transactions.id) AS 'number' "
					+ "FROM :prefix:transactions WHERE :prefix:transactions.ended BETWEEN ? AND ?"));
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			rs = pstmt.executeQuery();
			
			BigDecimal card = cardGiven(request, response);
			BigDecimal cashback = cashback(request, response);
			BigDecimal refunds = refunds(request, response);
			BigDecimal payouts = payouts(request, response);
			BigDecimal revenue = new BigDecimal(0);
			int numberOfTransactions = 0;
			if (rs.next()) {
				revenue = new BigDecimal(rs.getDouble(1));
				numberOfTransactions = rs.getInt(2);
			}
			JSONObject jo = new JSONObject();
			jo.put("revenue", revenue);
			jo.put("number", numberOfTransactions);
			jo.put("payouts", payouts);
			jo.put("card", card);
			jo.put("refunds", refunds);
			BigDecimal cashInDrawer = revenue;
			cashInDrawer = cashInDrawer.subtract(payouts);
			cashInDrawer = cashInDrawer.subtract(card);
			cashInDrawer = cashInDrawer.subtract(refunds);
			cashInDrawer = cashInDrawer.subtract(cashback.multiply(new BigDecimal(2)));
			jo.put("cashInDrawer", cashInDrawer);
			response.getWriter().write(jo.toJSONString());
		} catch (SQLException ex) {
			Log.error(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}
	private void getTakingsByDepartment(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		boolean dataOnly = request.getParameter("data-only").equals("false") ? false : true;
		String time_interval = request.getParameter("time_interval") != null ? request.getParameter("time_interval") : "HOUR";
		String startTimeString = request.getParameter("start");
		String endTimeString = request.getParameter("end");
		if (startTimeString == null || endTimeString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long startTime = 0L;
		Long endTime = 0L;
		try {
			startTime = Long.parseLong(startTimeString);
			endTime = Long.parseLong(endTimeString);
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray joArray = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:tblcatagories.name, SUM(:prefix:transactiontoproducts.price) AS 'value', :prefix:tblcatagories.colour "
					+ "FROM :prefix:transactiontoproducts RIGHT JOIN :prefix:tblcatagories ON :prefix:tblcatagories.id = :prefix:transactiontoproducts.department "
					+ "WHERE :prefix:transactiontoproducts.created BETWEEN ? AND ? GROUP BY :prefix:tblcatagories.id ORDER BY value DESC"));
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			rs = pstmt.executeQuery();
			if (!rs.isBeforeFirst()) {
				errorOut(response, "No data");
				return;
			}
			
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("name", rs.getString(1));
				jo.put("value", rs.getDouble(2));
				jo.put("colour", rs.getString(3));
				joArray.add(jo);
			}
		} catch (SQLException ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		if (dataOnly) {
			response.getWriter().write(joArray.toJSONString());
			return;
		}
		String[] labels = new String[joArray.size()];
		PieDataset dataset = new PieDataset().setBorderColor(Color.BLUE).setBorderWidth(1);
		int i = 0;
		Iterator<JSONObject> iter = joArray.iterator();
		while (iter.hasNext()) {
			JSONObject jo = iter.next();
			labels[i++] = (String) jo.get("name");
			dataset.addData((Double) jo.get("value"));
			dataset.addBackgroundColor(Color.random());
		}

		dataset.setLabel("Takings by department");
		
		Legend legend = new Legend();
		legend.setPosition(Position.RIGHT);
		legend.setDisplay(true);
		
		PieOptions options = new PieOptions();
		options.setResponsive(true);
		options.setMaintainAspectRatio(false);
		options.setTitle(null);
		options.setLegend(legend);

		PieData data = new PieData().addLabels(labels).addDataset(dataset);

		response.getWriter().write(new PieChart(data, options).toJson());
	}

	private void getOperatorTotals(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String time_interval = request.getParameter("time_interval") != null ? request.getParameter("time_interval") : "HOUR";
		String startTimeString = request.getParameter("start");
		String endTimeString = request.getParameter("end");
		if (startTimeString == null || endTimeString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long startTime = 0L;
		Long endTime = 0L;
		try {
			startTime = Long.parseLong(startTimeString);
			endTime = Long.parseLong(endTimeString);
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix("SELECT :prefix:operators.name, SUM(:prefix:transactions.total) AS 'value' FROM :prefix:operators "
					+ "RIGHT JOIN :prefix:transactions ON :prefix:operators.id = :prefix:transactions.cashier WHERE :prefix:transactions.ended BETWEEN ? AND ? "
					+ "GROUP BY :prefix:operators.id ORDER BY value DESC"));
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			rs = pstmt.executeQuery();
			if (!rs.isBeforeFirst()) {
				errorOut(response, "No data");
				return;
			}
			JSONArray resp = new JSONArray();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", rs.getString(1));
				jsonObject.put("value", rs.getFloat(2));
				resp.add(jsonObject);
			}
			response.getWriter().write(resp.toJSONString());
			return;
		} catch (SQLException ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}

	private void generateTakingsGraph(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String time_interval = request.getParameter("time_interval") != null ? request.getParameter("time_interval") : "HOUR";
		String startTimeString = request.getParameter("start");
		String endTimeString = request.getParameter("end");
		if (startTimeString == null || endTimeString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Long startTime = 0L;
		Long endTime = 0L;
		try {
			startTime = Long.parseLong(startTimeString);
			endTime = Long.parseLong(endTimeString);
		} catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		Double[] values = null;
		int[] transactionCounts = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String sql = null;
			switch (time_interval) {
				case "HOUR":
					sql = "SELECT HOUR(FROM_UNIXTIME(ended)) AS hour, SUM(total) AS total, COUNT(id) AS count FROM :prefix:transactions WHERE ended BETWEEN ? AND ? GROUP BY hour ORDER BY hour";
					values = new Double[24];
					transactionCounts = new int[24];
					break;
				case "DAY":
					sql = "SELECT DAY(FROM_UNIXTIME(ended)) AS day, SUM(total) AS total, COUNT(id) AS count FROM :prefix:transactions WHERE ended BETWEEN ? AND ? GROUP BY day ORDER BY day";
					values = new Double[31];
					transactionCounts = new int[31];
					break;
				case "WEEK":
					sql = "SELECT WEEK(FROM_UNIXTIME(ended)) AS week, SUM(total) AS total, COUNT(id) AS count FROM :prefix:transactions WHERE ended BETWEEN ? AND ? GROUP BY week ORDER BY week";
					values = new Double[5];
					transactionCounts = new int[5];
					break;
				case "MONTH":
					sql = "SELECT MONTH(FROM_UNIXTIME(ended)) AS month, SUM(total) AS total, COUNT(id) AS count FROM :prefix:transactions WHERE ended BETWEEN ? AND ? GROUP BY month ORDER BY month";
					values = new Double[12];
					transactionCounts = new int[12];
					break;
				default:
					sql = "SELECT HOUR(FROM_UNIXTIME(ended)) AS hour, SUM(total) AS total, COUNT(id) AS count FROM :prefix:transactions WHERE ended BETWEEN ? AND ? GROUP BY hour ORDER BY hour";
					values = new Double[24];
					transactionCounts = new int[24];
					break;
			}
			Arrays.fill(values, 0.0);
			Arrays.fill(transactionCounts, 0);
			pstmt = conn.prepareStatement(Utils.addTablePrefix(sql));
			
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				values[rs.getInt(1)] = rs.getDouble(2);
				transactionCounts[rs.getInt(1)] = rs.getInt(3);
			}
		} catch (SQLException ex) {
			Log.info(ex.getMessage());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		
		BarDataset totals = new BarDataset().setBorderColor(Color.GREEN).setBorderWidth(1);
		for (Double value : values) {
			totals.addData(value);
		}
		totals.setLabel("Takings");
		
		BarDataset transactions = new BarDataset().setBorderColor(Color.BLUE).setBorderWidth(1);
		for (int value : transactionCounts) {
			transactions.addData(value);
		}
		transactions.setLabel("Transactions");
		
		BarOptions options = new BarOptions();
		options.setResponsive(true);
		options.setMaintainAspectRatio(false);
		options.setTitle(null);
		int valuesLen = values.length;
		String[] labels = new String[valuesLen];
		for (int i=0;i<valuesLen;i++) {
			labels[i] = String.format("%s:00", Integer.toString(i));
		}
		BarData data = new BarData().addLabels(labels).addDataset(transactions).addDataset(totals);

		response.getWriter().write(new BarChart(data, options).toJson());
	}

	private void generateLabelsPDF(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String includeLabelledString = request.getParameter("includeLabelled") == null ? "true" : request.getParameter("includeLabelled");
		boolean includeLabelled = true;
		if (includeLabelledString.equals("false")) {
			includeLabelled = false;
		}
		String[] products = request.getParameterValues("products[]");
		if (products == null) {
			errorOut(response, "missing parameters");
			return;
		}
		String filename = PDFHelper.createLabelsPDF(products, includeLabelled);
		JSONObject jo = new JSONObject();
		jo.put("success", true);
		jo.put("file", filename);
		response.getWriter().write(jo.toJSONString());
	}

	private void getLabels(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		notImplementedOut(response);
	}

	private void notImplementedOut(HttpServletResponse response) throws IOException, ServletException {
		errorOut(response, "Not Implemented");
	}

	private void getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String sessionId = getAuthCookieValue(request);
		if (sessionId == null) {
			errorOut(response, "Not Logged In");
			return;
		}
		CustomUser user = this.sessionHandler.getSessionValue(getAuthCookieValue(request));
		if (user == null) {
			errorOut(response, "Unkown User");
			return;
		}
		JSONObject jo = new JSONObject();
		jo.put("id", user.getId());
		jo.put("name", user.getName());
		switch (user.getType()) {
			case 0:
				jo.put("type", "Unknown");
				break;
			case 1:
				jo.put("type", "Operator");
				break;
			case 2:
				jo.put("type", "Manager");
				break;
			case 3:
				jo.put("type", "Administrator");
				break;
		}
		jo.put("email", user.getEmail());
		jo.put("telephone", user.getTelephone());
		response.getWriter().write(jo.toJSONString());
	}
	
	private void getOrderForSupplier(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String supplier = request.getParameter("supplier");
		if (supplier == null) {
			errorOut(response, "missing feilds");
		}
		Order.getOrderForSupplier(supplier);
	}

	private void getTakingsChart(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		response.getWriter().write(ChartHelper.generateTakingsChart());
	}
}
