package com.opentill.httpServer;

//
//========================================================================
//Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//All rights reserved. This program and the accompanying materials
//are made available under the terms of the Eclipse Public License v1.0
//and Apache License v2.0 which accompanies this distribution.
//
//  The Eclipse Public License is available at
//  http://www.eclipse.org/legal/epl-v10.html
//
//  The Apache License v2.0 is available at
//  http://www.opensource.org/licenses/apache2.0.php
//
//You may elect to redistribute this code under either of these licenses.
//========================================================================
//

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.products.Product;
import com.opentill.database.DatabaseHandler;
import com.opentill.document.ExcelHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.eclipse.jetty.server.Request;

public class API extends ContextHandler
{
	private SessionHandler sessionHandler;
	public API (WebAppContext webAppContext, String context) {
		super.setContextPath(context);
		super.setAllowNullPathInfo(true); ///Allows Post
		this.sessionHandler = webAppContext.getSessionHandler();
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		if (baseRequest.getSessionHandler() == null) {
			SessionHandler handler = new SessionHandler();
			handler.setMaxInactiveInterval(3600);
			baseRequest.setSessionHandler(handler);
		}
		switch (baseRequest.getParameter("function")) {
			case "BARCODE":
				barcode(baseRequest, response);
				break;
			case "TRANSACTION":
				startTransaction(baseRequest, response);
				break;
			case "DEPARTMENTS":
				getDepartments(response);
				break;
			case "LOGIN":
				login(baseRequest, response);
				break;
			case "LOGOUT":
				logout(baseRequest, response);
				break;
			case "GETTRANSACTION":
				getTransactionProducts(baseRequest, response);
				break;
			case "GETPRODUCTSALES":
				getProductTransactions(baseRequest, response);
				break;
			case "GETTRANSACTIONS":
				getTransactions(baseRequest, response);
				break;
			case "GETDAYTOTALS":
				getDayTotals(baseRequest, response);
				break;
			case "COMPLETETRANSACTION":
				completeTransaction(baseRequest, response);
				break;
			case "CLEARTRANSACTION":
				cancelTransaction(baseRequest, response);
				break;
			case "UPDATEPRODUCT":
				updateProduct(baseRequest, response);
				break;
			case "GETPRODUCT":
				getProduct(baseRequest, response);
				break;
			case "PRINTLABEL":
				printLabel(baseRequest, response);
				break;
			case "GETALLSUPPLIERS":
				getAllSuppliers(baseRequest, response);
				break;
			case "GENERATETAKINGSREPORT":
				generateTakingsReport(baseRequest, response);
				break;
			case "GENERATEINVENTORYREPORT":
				generateInventoryReport(baseRequest, response);
				break;
			case "GETSUPPLIER":
				selectSupplier(baseRequest, response);
				break;
			case "UPDATESUPPLIER":
				updateSupplier(baseRequest, response);
				break;
			case "ADDSUPPLIER":
				createSupplier(baseRequest, response);
				break;
			case "DELETESUPPLIER":
				//deleteSupplier(baseRequest, response);
				break;
			case "GETALLOPERATORS":
				getAllOperators(baseRequest, response);
				break;
			case "GETOPERATOR":
				selectOperator(baseRequest, response);
				break;
			case "UPDATEOPERATOR":
				updateOperator(baseRequest, response);
				break;
			case "ADDOPERATOR":
				createOperator(baseRequest, response);
				break;
			case "DELETEOPERATOR":
				//deleteOperator(baseRequest, response);
				break;
			case "GETALLDEPARTMENTS":
				getAllDepartments(baseRequest, response);
				break;
			case "GETDEPARTMENT":
				selectDepartment(baseRequest, response);
				break;
			case "UPDATEDEPARTMENT":
				//updateDepartment(baseRequest, response);
				break;
			case "ADDDEPARTMENT":
				createDepartment(baseRequest, response);
				break;
			case "DELETEDEPARTMENT":
				deleteDepartment(baseRequest, response);
				break;
			case "DELETEPRODUCT":
				deleteProduct(baseRequest, response);
				break;
			case "SEARCH":
				search(baseRequest, response);
				break;
			case "TAKINGS":
				getTakings(baseRequest, response);
				break;
			case "CLEARLABELS":
				//clearLabels(baseRequest, response);
				break;
			case "OPERATORLOGON":
				operatorLogin(baseRequest, response);
				break;
			case "TOTALS":
				getTakings(baseRequest, response);
				break;
			case "SENDMESSAGE":
				sendMessage(baseRequest, response);
				break;
			case "GETMESSAGES":
				getMessages(baseRequest, response);
				break;
			case "SAVELABELSTYLE":
				//saveLabel(baseRequest, response);
				break;
			case "GETLABELSTYLE":
				//getLabelStyle(baseRequest, response);
				break;
			case "GETLABELSTYLES":
				//getLabelStyles(baseRequest, response);
				break;
			case "ISLOGGEDIN":
				//isLoggedIn(baseRequest, response);
				break;
			case "GETPRODUCTLEVELS":
				//getProductLevels(baseRequest, response);
				break;
			case "GETPRODUCTSLEVELS":
				getProductsLevels(baseRequest, response);
				break;
			case "CHANGEMAXSTOCKLEVEL":
				setMaxStockLevel(baseRequest, response);
				break;
			case "CHANGECURRENTSTOCKLEVEL":
				setCurrentStockLevel(baseRequest, response);
				break;
			case "CREATEORDER":
				createOrder(baseRequest, response);
				break;
			case "COMPLETEORDER":
				completeOrder(baseRequest, response);
				break;
			case "GETORDER":
				getOrder(baseRequest, response);
				break;
			case "GETORDERS":
				getOrders(baseRequest, response);
				break;
			case "ADDPRODUCTTOORDER":
				addProductToOrder(baseRequest, response);
				break;
			default:
				errorOut(response, "No such function");
				break;
		}
	    // Inform jetty that this request has now been handled
	    baseRequest.setHandled(true);
	}
	private void completeOrder(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing id");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblorders SET ended = ? WHERE id = ?");
			pstmt.setLong(1, getCurrentTimeStamp());
			pstmt.setString(2, id);
			if (pstmt.executeUpdate() > 0) {
				Log.log("Order ($id) COMPLETED by operator ($operator)");
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void addProductToOrder(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
		}
		else {
			try {
				quantity = Integer.parseInt(quantityString);
			}
			catch (NumberFormatException e) {
				errorOut(response, "Could not parse quantity");
				return;
			}
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "tblorders_to_products (productId, orderId, quantity, created, updated) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE quantity = quantity + 1");
			JSONObject productJson;
			if (productBarcode != null) {
				productJson = getItemFromBarcode(productBarcode);
				if (productJson == null) {
					errorOut(response, "Could not find product");
					return;
				}
				pstmt.setString(1, (String) productJson.get("id"));
			}
			else {
				pstmt.setString(1, productId);
			}
			pstmt.setString(2, order);
			pstmt.setInt(3, quantity);
			pstmt.setLong(4, getCurrentTimeStamp());
			pstmt.setLong(5, getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void getOrder(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name, "+ Config.DATABASE_TABLE_PREFIX + "tblorders_to_products.productId, " + Config.DATABASE_TABLE_PREFIX + "tblorders_to_products.quantity FROM   "+ Config.DATABASE_TABLE_PREFIX + "tblorders_to_products LEFT JOIN "+ Config.DATABASE_TABLE_PREFIX + "tblproducts ON "+ Config.DATABASE_TABLE_PREFIX + "tblproducts.id = "+ Config.DATABASE_TABLE_PREFIX + "tblorders_to_products.productId WHERE "+ Config.DATABASE_TABLE_PREFIX + "tblorders_to_products.orderId = ?");
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
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void getOrders(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("orders", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void createOrder(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String supplier = baseRequest.getParameter("supplier");
		if (supplier == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "tblorders (id, supplier, created, updated, ended) VALUES (?, ?, ?, ?, 0)");
			pstmt.setString(1, GUID());
			pstmt.setString(2, supplier);
			pstmt.setLong(3, getCurrentTimeStamp());
			pstmt.setLong(4, getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void updateOperator(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "operators SET name=?, passwordHash=?, telephone=?, email=?, comments=?, updated=?) WHERE id=?");
			pstmt.setString(1, name);
			pstmt.setString(2, hashPassword(password, ""));
			pstmt.setString(3, telephone);
			pstmt.setString(4, email);
			pstmt.setString(5, comments);
			pstmt.setInt(6,  getCurrentTimeStamp());
			pstmt.setString(7, id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void createSupplier(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers (id, name, telephone, email, website, comments, created, updated) VALUES (?,?,?,?,?,?,?,?)");
			pstmt.setString(1, GUID());
			pstmt.setString(2, name);
			pstmt.setString(3, telephone);
			pstmt.setString(4, email);
			pstmt.setString(5, website);
			pstmt.setString(6, comments);
			pstmt.setLong(7, getCurrentTimeStamp());
			pstmt.setLong(8, getCurrentTimeStamp());
			if (pstmt.executeUpdate() > 0) {
				Log.log("Supplier ($id) CREATED by operator ($operator)");
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void updateSupplier(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers SET name = ?, telephone = ?, email = ?, website = ?, comments = ?, updated = ? WHERE id = ? LIMIT 1");
			pstmt.setString(1, name);
			pstmt.setString(2, telephone);
			pstmt.setString(3, email);
			pstmt.setString(4, website);
			pstmt.setString(5, comments);
			pstmt.setLong(6, getCurrentTimeStamp());
			pstmt.setString(7, id);
			if (pstmt.executeUpdate() > 0) {
				Log.log("Supplier ($id) UPDATED by operator ($operator)");
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
		
	}
	private void selectSupplier(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT name, telephone, website, email, comments FROM " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers WHERE id = ? LIMIT 1");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			if (rs.next()) {
				jo.put("name", rs.getString(1));
				jo.put("telephone", rs.getString(2));
				jo.put("website", rs.getString(3));
				jo.put("email", rs.getString(4));
				jo.put("comments", rs.getString(5));
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("supplier", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void selectOperator(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT name, telephone, email, comments, code FROM " + Config.DATABASE_TABLE_PREFIX + "operators WHERE id = ? LIMIT 1");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			if (rs.next()) {
				jo.put("name", rs.getString(1));
				jo.put("telephone", rs.getString(2));
				jo.put("email", rs.getString(3));
				jo.put("comments", rs.getString(4));
				jo.put("code", rs.getString(5));
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("operator", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void createOperator(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String name = baseRequest.getParameter("name");
		String telephone = baseRequest.getParameter("telephone");
		String email = baseRequest.getParameter("email");
		String password = baseRequest.getParameter("password");
		String comments = baseRequest.getParameter("comments");
		if ((name == null) || (email == null) || (password == null)) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			String guid = GUID();
			String code = GUID().split("-")[0];
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "operators (id, code, name, passwordHash, telephone, email, comments, created, updated) VALUES (?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, guid);
			pstmt.setString(2, code);
			pstmt.setString(3, name);
			pstmt.setString(4, hashPassword(password, ""));
			pstmt.setString(5, telephone);
			pstmt.setString(6, email);
			pstmt.setString(7, comments);
			pstmt.setInt(8,  getCurrentTimeStamp());
			pstmt.setInt(9,  getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void selectDepartment(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT id, name, shorthand, comments, colour FROM " + Config.DATABASE_TABLE_PREFIX + "tblcatagories WHERE id = ? LIMIT 1");
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
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void deleteDepartment(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories SET deleted = 1 WHERE id = ? LIMIT 1");
			pstmt.setString(1, id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
		
	}
	private void createDepartment(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "tblcatagories (id, name, shorthand, colour, comments, created, updated) VALUES (?,?,?,?,?,?,?)");
			pstmt.setString(1, guid);
			pstmt.setString(2, name);
			pstmt.setString(3, shorthand);
			pstmt.setString(4,  colour);
			pstmt.setString(5,  comments);
			pstmt.setInt(6,  getCurrentTimeStamp());
			pstmt.setInt(7,  getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void generateInventoryReport(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.id, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.current_stock, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.max_stock FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblcatagories ON " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department = " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id WHERE " + Config.DATABASE_TABLE_PREFIX + "tblproducts.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name");
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
				}
				else {
					if (Arrays.asList(departments).indexOf(rs.getString(1)) > -1) {
						HashMap<String, Product> department = new HashMap<String, Product>();
						department.put(rs.getString(2), tempProduct);
						inventory.put(rs.getString(1), department);					
					}
				}
			}
			DatabaseHandler.closeDBResources(rs, pstmt, null);
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name FROM " + Config.DATABASE_TABLE_PREFIX + "tblcatagories WHERE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			rs = pstmt.executeQuery();
			HashMap<String, String> departmentsToNames = new HashMap<String, String>();
			while (rs.next()) {
				departmentsToNames.put(rs.getString(1), rs.getString(2));
			}
			new ExcelHelper().createProductLevelsReport(departmentsToNames, departments, inventory);
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		successOut(response);
	}
	private void deleteProduct(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing params");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET deleted = 1, updated=? WHERE id=?");
			pstmt.setLong(1, getCurrentTimeStamp());
			pstmt.setString(2, id);
			if (pstmt.executeUpdate() > 0) {
				Log.log("Product ($id) DELETED by operator ($operator)");
				successOut(response);
				return;
			}
			errorOut(response);
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}
	private void generateTakingsReport(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
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
			startTime = new Date(startTimeString).getTime();
			endTime = new Date(endTimeString).getTime();
		}
		catch (NumberFormatException e) {
			errorOut(response, "Could not parse time");
			return;
		}
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) AS \"date\", " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department, SUM(" + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.price) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "transactions ON " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = " + Config.DATABASE_TABLE_PREFIX + "transactions.id WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0) GROUP BY " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department, DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) ORDER BY DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) DESC");
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
				}
				else {
					HashMap<String, Double> date = new HashMap<String, Double>();
					date.put(rs.getString(2), rs.getDouble(3));
					if (Arrays.asList(departments).indexOf(rs.getString(2)) > -1) {
						allDates.put(rs.getString(1), date);
					}
				}
			}
			pstmt.close();
			rs.close();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name FROM " + Config.DATABASE_TABLE_PREFIX + "tblcatagories WHERE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			rs = pstmt.executeQuery();
			HashMap<String, String> departmentsToNames = new HashMap<String, String>();
			while (rs.next()) {
				departmentsToNames.put(rs.getString(1), rs.getString(2));
			}
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
			new ExcelHelper().createTakingsReport(departmentsToNames, departments, allDates);
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		successOut(response);
	}
	private void getAllDepartments(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name FROM " + Config.DATABASE_TABLE_PREFIX + "tblcatagories WHERE " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			rs = pstmt.executeQuery();
			JSONObject departments = new JSONObject();
			while (rs.next()) {
				departments.put(rs.getString(1), rs.getString(2));
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("departments", departments);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void getAllOperators(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "operators.id, " + Config.DATABASE_TABLE_PREFIX + "operators.name FROM " + Config.DATABASE_TABLE_PREFIX + "operators WHERE " + Config.DATABASE_TABLE_PREFIX + "operators.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "operators.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				JSONObject product = new JSONObject();
				product.put("name",  rs.getString(2));
				jo.put(rs.getString(1), product);
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("operators", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void getAllSuppliers(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.id, " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name FROM " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers WHERE " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.deleted = 0 ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblsuppliers.name");
			rs = pstmt.executeQuery();
			JSONObject jo = new JSONObject();
			while (rs.next()) {
				jo.put(rs.getString(1), rs.getString(2));
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("suppliers", jo);
			response.getWriter().write(responseJSON.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void sendMessage(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("INSERT " + Config.DATABASE_TABLE_PREFIX + "tblchat (id, sender, recipient, message, updated, created) VALUES (?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, GUID());
			pstmt.setString(2, sender);
			pstmt.setString(3,  recipient);
			pstmt.setString(4,  message);
			pstmt.setInt(5,  getCurrentTimeStamp());
			pstmt.setInt(6,  getCurrentTimeStamp());
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void printLabel(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing parameters");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET labelprinted = 1 WHERE id = ?");
			pstmt.setString(1, id);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void getProductsLevels(Request baseRequest, HttpServletResponse response) {
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
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("success", true);
			responseJSON.put("products", jo);
			response.getWriter().write(responseJSON.toJSONString());
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}
	private void setCurrentStockLevel(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET current_stock=? WHERE id = ?");
			pstmt.setString(1, id);
			pstmt.setInt(1, amount);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
			errorOut(response);
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}
	
	private void setMaxStockLevel(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
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
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET max_stock=? WHERE id = ?");
			pstmt.setString(1, id);
			pstmt.setInt(1, amount);
			pstmt.execute();
			if (pstmt.getUpdateCount() > 0) {
				successOut(response);
				return;
			}
			errorOut(response);
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}
	private void getProduct(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing fields");
		}
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblproducts.id, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.price, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.barcode,"
					+ " " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.max_stock, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.current_stock FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts WHERE id = ? LIMIT 1");
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
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}
	private void getTakings(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		String type = baseRequest.getParameter("type") == null ? "PURCHASE" : baseRequest.getParameter("type");
		if (startString == null || endString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) AS \"date\", " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department, SUM(" + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.price) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "transactions ON " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = " + Config.DATABASE_TABLE_PREFIX + "transactions.id WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0) GROUP BY " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department, DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) ORDER BY DATE(FROM_UNIXTIME(" + Config.DATABASE_TABLE_PREFIX + "transactions.ended)) DESC");
			pstmt.setLong(1, Long.parseLong(startString));
			pstmt.setLong(2, Long.parseLong(endString));
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
			JSONObject jo = new JSONObject();
			jo.put("success", true);
			jo.put("totals", allDates);
			response.getWriter().write(jo.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}
	@SuppressWarnings("unchecked")
	private void search(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblproducts.id, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.barcode, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.price FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts WHERE LOWER(name) LIKE LOWER(?) AND deleted = 0 ORDER BY name LIMIT 20");
			pstmt.setString(1, "%" + search + "%");
			rs = pstmt.executeQuery();
			JSONObject responseJson = new JSONObject();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("name",  rs.getString(2));
				jo.put("barcode",  rs.getString(3));
				jo.put("price",  rs.getDouble(4));
				responseJson.put(rs.getString(1), jo);
			}
			response.getWriter().write(responseJson.toJSONString());
			return;
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}
	private void updateProduct(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
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
		if (name==null || priceText==null || department==null) {
			errorOut(response, "missing fields");
			return;
		}
		float price; 
		int currentStockInt = 0;
		int maxStockInt = 0;
		try {
			price = Float.parseFloat(priceText);
			currentStockInt = Integer.parseInt(currentStockString);
			maxStockInt = Integer.parseInt(maxStockString);
		}
		catch (NumberFormatException ex) {
			Log.log(ex.getMessage());
			errorOut(response, "Could not interpret numeric field");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET name = ?, price = ?, department=?, current_stock=?, max_stock=?, updated = ? WHERE id=?");
			pstmt.setString(1, name);
			pstmt.setFloat(2, price);
			pstmt.setString(3, department);
			pstmt.setInt(4, currentStockInt);
			pstmt.setInt(5, maxStockInt);
			pstmt.setLong(6, getCurrentTimeStamp());
			pstmt.setString(7, id);
			if (pstmt.executeUpdate() > 0) {
				Log.log("Product ($id) UPDATED by operator ($operator)");
				successOut(response);
				return;
			}
			errorOut(response);
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}
	private void cancelTransaction(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String id = baseRequest.getParameter("transaction_id");
		if (!transactionExists(id)) {
			errorOut(response, "transaction does not exist");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("DELETE FROM " + Config.DATABASE_TABLE_PREFIX + "transactions WHERE (id = ? AND ended = 0) LIMIT 1");
			pstmt.setString(1, id);
			if (pstmt.executeUpdate() > 0) {
				successOut(response);
				return;
			}
		}
		catch (Exception ex) {
			Log.log(ex.getMessage());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		errorOut(response);
	}
	private void completeTransaction(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String id = baseRequest.getParameter("id");
		float money_given = (float) (baseRequest.getParameter("money_given") == null ? 0.00 : Float.parseFloat(baseRequest.getParameter("money_given")));
		float total = (float) (baseRequest.getParameter("total") == null ? 0.00 : Float.parseFloat(baseRequest.getParameter("total")));
		float card = (float) (baseRequest.getParameter("card") == null ? 0.00 : Float.parseFloat(baseRequest.getParameter("card")));
		float cashback = (float) (baseRequest.getParameter("cashback") == null ? 0.00 : Float.parseFloat(baseRequest.getParameter("cashback")));
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
				Log.log("Failed to write transaction(" + id + ") to database, rolling back");
				errorOut(response);
				return;
			}
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "transactions SET ended = ?, total = ?, cashback=?, money_given = ?, card = ?, type=?, payee=? WHERE id=? AND cashier=?");
			pstmt.setLong(1, getCurrentTimeStamp());
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
				Log.log("Transaction " + id + " COMPLETED by operator (" + cashier_id + ")");
				successOut(response);
				return;
			}
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}
	private boolean writeTransactionFileToDatabase(String id, String json, String type) throws SQLException {
		// TODO Auto-generated method stub
		if (json==null) {
			return false;
		}
		JSONObject jo = null;
		try {
			JSONParser parser = new JSONParser();
			jo = (JSONObject) parser.parse(json);
		}
		catch (ParseException ex) {
			Log.log(ex.getMessage());
			return false;
		}
		Connection conn =  null;
		try {
			conn = DatabaseHandler.getDatabase();
			conn.setAutoCommit(false);
			JSONArray products = (JSONArray) jo.get("products");
			for (int i=0;i<products.size();i++) {
				JSONObject joProduct = (JSONObject) products.get(i);
				Long productQuantity = (Long) joProduct.get("quantity");
				if (productQuantity == 0L) {
					Log.log("Error, transaction (" + id + ") tried to buy 0 products (" + joProduct.get("id") + ")");
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
				//TODO:: USE A DIFFERENT LIBRARY TO HANDLE THE TRANSACTION
				while (productQuantity-- > 0) {
					if (!insertTransactionProduct(conn, id, ((boolean) joProduct.get("inDatabase") ? (String) joProduct.get("id"): null), (double) joProduct.get("price"), (String) joProduct.get("department"))) {
						throw new Exception();
					}
				}
			}
			conn.commit();
			return true;
		}
		catch (Exception ex) {
			conn.rollback();
			Log.log(ex.toString());
			return false;
		}
		finally {
			DatabaseHandler.closeDBResources(null, null, conn);
		}
	}
	private boolean insertTransactionProduct(Connection conn, String transactionId, String productId, double price, String departmentId) throws SQLException {
		String noneCatagory = "5b82f89a-7b71-11e7-b34e-426562cc935f";
		PreparedStatement pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts (id, transaction_id, product_id, price, department, created) VALUES (?, ?, ?, ?, ?, ?)");
		pstmt.setString(1, GUID());
		pstmt.setString(2, transactionId);
		pstmt.setString(3, productId);
		pstmt.setDouble(4, price);
		pstmt.setString(5, departmentId == null ? noneCatagory : departmentId);
		pstmt.setLong(6, getCurrentTimeStamp());
		pstmt.execute();
		if (pstmt.getUpdateCount() > 0) {
			return true;
		}
		return false;
	}
	private boolean incrementProductLevel(Connection conn, String productId, Long productQuantity) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET current_stock=current_stock+? WHERE id = ?");
		pstmt.setLong(1, productQuantity);
		pstmt.setString(2, productId);
		if (pstmt.executeUpdate() > 0) {
			return true;
		}
		return false;
	}
	private boolean decrementProductLevel(Connection conn, String productId, Long productQuantity) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement pstmt = conn.prepareStatement("UPDATE " + Config.DATABASE_TABLE_PREFIX + "tblproducts SET current_stock=current_stock-? WHERE id = ? AND current_stock > 0");
		pstmt.setLong(1, productQuantity);
		pstmt.setString(2, productId);
		if (pstmt.executeUpdate() > 0) {
			return true;
		}
		return false;
	}
	private int getCurrentTimeStamp() {
		// TODO Auto-generated method stub
		return Math.round(System.currentTimeMillis() / 1000);
	}
	private boolean transactionExists(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// TODO Auto-generated method stub
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT 1 FROM " + Config.DATABASE_TABLE_PREFIX + "transactions WHERE (id = ? AND ended = 0) LIMIT 1");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		}
		catch (Exception ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	private void getDayTotals(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		JSONObject jo = new JSONObject();
		jo.put("success", true);
		jo.put("card", cardGiven(baseRequest, response));
		jo.put("cashback", cashback(baseRequest, response));
		jo.put("takings", takings(baseRequest, response));
		jo.put("refunds", refunds(baseRequest, response));
		jo.put("payouts", payouts(baseRequest, response));
		response.getWriter().print(jo.toJSONString());
	}
	
	private float payouts(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT IFNULL(SUM(" + Config.DATABASE_TABLE_PREFIX + "transactions.total), 0.00) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactions WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0)");
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PAYOUT");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getFloat(1);
			}
		}
		catch (Exception ex) {
			Log.log(ex.getMessage());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return 0.00F;
	}
	private float refunds(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT SUM(" + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.price) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "transactions ON " + Config.DATABASE_TABLE_PREFIX + "transactions.id = " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0) GROUP BY " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department");
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "REFUND");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getFloat(1);
			}
		}
		catch (Exception ex) {
			Log.log(ex.getMessage());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return 0.00F;
	}
	private float takings(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT SUM(" + Config.DATABASE_TABLE_PREFIX + "transactions.total) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactions WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0)");
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getFloat(1);
			}
		}
		catch (Exception ex) {
			Log.log(ex.getMessage());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return 0.00F;
	}
	private float cashback(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT SUM(" + Config.DATABASE_TABLE_PREFIX + "transactions.cashback) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactions WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0)");
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getFloat(1);
			}
		}
		catch (Exception ex) {
			Log.log(ex.getMessage());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return 0.00F;
	}
	private float cardGiven(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
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
			pstmt = conn.prepareStatement("SELECT SUM(" + Config.DATABASE_TABLE_PREFIX + "transactions.card) AS \"amount\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactions WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.started > ? AND " + Config.DATABASE_TABLE_PREFIX + "transactions.ended < ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.type in (?) AND (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended > 0)");
			pstmt.setFloat(1, Float.parseFloat(startString));
			pstmt.setFloat(2, Float.parseFloat(endString));
			pstmt.setString(3, admin);
			pstmt.setString(4, "PURCHASE");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getFloat(1);
			}
		}
		catch (Exception ex) {
			Log.log(ex.getMessage());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return 0.00F;
	}
	@SuppressWarnings("unchecked")
	private void getTransactions(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String startString = baseRequest.getParameter("start");
		String endString = baseRequest.getParameter("end");
		if (startString == null || endString == null) {
			errorOut(response, "missing parameters");
			return;
		}
		int start = 0; 
		int end = 0;
		try {
			start = Integer.parseInt(startString);
			end = Integer.parseInt(endString);
		}
		catch (NumberFormatException ex) {
			errorOut(response, "Could not parse start or end");
			return;
		}
		try {
			JSONArray jsonArr = new JSONArray();
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "transactions.id AS 'id', " + Config.DATABASE_TABLE_PREFIX + "operators.name AS cashier, (SELECT COUNT(*) FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts WHERE " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = " + Config.DATABASE_TABLE_PREFIX + "transactions.id) AS '#Products', " + Config.DATABASE_TABLE_PREFIX + "transactions.card AS 'card', " + Config.DATABASE_TABLE_PREFIX + "transactions.ended AS 'ended', " + Config.DATABASE_TABLE_PREFIX + "transactions.cashback AS 'cashback', " + Config.DATABASE_TABLE_PREFIX + "transactions.money_given AS 'money_given', " + Config.DATABASE_TABLE_PREFIX + "transactions.payee AS 'payee', " + Config.DATABASE_TABLE_PREFIX + "transactions.type AS 'type', " + Config.DATABASE_TABLE_PREFIX + "transactions.total AS 'total' FROM " + Config.DATABASE_TABLE_PREFIX + "transactions LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "operators ON " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier = " + Config.DATABASE_TABLE_PREFIX + "operators.id WHERE (" + Config.DATABASE_TABLE_PREFIX + "transactions.ended BETWEEN ? AND ?) AND " + Config.DATABASE_TABLE_PREFIX + "transactions.cashier NOT IN (?) ORDER BY " + Config.DATABASE_TABLE_PREFIX + "transactions.type DESC, " + Config.DATABASE_TABLE_PREFIX + "transactions.ended");
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			pstmt.setString(3, "a10f653a-6c20-11e7-b34e-426562cc935f"); // Admin
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs.getString(1));
				jsonObject.put("cashier", rs.getString(2));
				jsonObject.put("numProducts", rs.getInt(3));
				jsonObject.put("card", rs.getFloat(4));
				jsonObject.put("ended", rs.getInt(5));
				jsonObject.put("cashback", rs.getFloat(6));
				jsonObject.put("money_given", rs.getFloat(7));
				jsonObject.put("payee", rs.getString(8));
				jsonObject.put("type", rs.getString(9));
				jsonObject.put("total", rs.getFloat(10));
				jsonArr.add(jsonObject);
			}
			JSONObject responseJson = new JSONObject();
			responseJson.put("success", true);
			responseJson.put("transactions", jsonArr);
			response.getWriter().write(responseJson.toJSONString());
			return;
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
		/*$admin = "a10f653a-6c20-11e7-b34e-426562cc935f";
		$start = get_param("start", null);
		$end = get_param("end", null);
		if ($start == null || ($end == null)) {
			error_out("missing fields");
		}
		$db = get_pdo_connection();
		$stmt = $db->prepare("SELECT " + Config.DATABASE_PREFIX + "transactions.id AS "id", " + Config.DATABASE_PREFIX + "operators.name AS cashier, (SELECT COUNT(*) FROM " + Config.DATABASE_PREFIX + "transactiontoproducts WHERE " + Config.DATABASE_PREFIX + "transactiontoproducts.transaction_id = " + Config.DATABASE_PREFIX + "transactions.id) AS "#Products", " + Config.DATABASE_PREFIX + "transactions.card AS "card", " + Config.DATABASE_PREFIX + "transactions.ended AS "ended", " + Config.DATABASE_PREFIX + "transactions.cashback AS "cashback", " + Config.DATABASE_PREFIX + "transactions.money_given AS "money_given", " + Config.DATABASE_PREFIX + "transactions.payee AS "payee", " + Config.DATABASE_PREFIX + "transactions.type AS "type", " + Config.DATABASE_PREFIX + "transactions.total AS "total" FROM " + Config.DATABASE_PREFIX + "transactions LEFT JOIN " + Config.DATABASE_PREFIX + "operators ON " + Config.DATABASE_PREFIX + "transactions.cashier = " + Config.DATABASE_PREFIX + "operators.id WHERE (" + Config.DATABASE_PREFIX + "transactions.ended BETWEEN ? AND ?) AND " + Config.DATABASE_PREFIX + "transactions.cashier NOT IN (?) ORDER BY " + Config.DATABASE_PREFIX + "transactions.type DESC, " + Config.DATABASE_PREFIX + "transactions.ended');
		$stmt->bindValue(1, $start, PDO::PARAM_INT);
		$stmt->bindValue(2, $end, PDO::PARAM_INT);
		$stmt->bindValue(3, $admin, PDO::PARAM_STR);
		$stmt->execute();
		$arr = array();
		while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
			array_push($arr, $rs);
		}
		die (json_encode(array("success"=>true, "start"=>$start, "end"=>$end, "transactions"=>$arr)));	*/
	}
	private void getProductTransactions(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
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
		}
		catch (NumberFormatException ex) {
			errorOut(response, "Could not parse start or end");
			return;
		}
		try {
			JSONArray jsonArr = new JSONArray();
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT * FROM `" + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts` WHERE product_id = ? AND created BETWEEN ? AND ?");
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
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}
	@SuppressWarnings("unchecked")
	private void getTransactionProducts(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String id = baseRequest.getParameter("id");
		if (id == null) {
			errorOut(response, "missing parameters");
			return;
		}
		try {
			JSONArray jsonArr = new JSONArray();
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.product_id, COUNT(*) AS \"quantity\", " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.price, IFNULL((SELECT name FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts WHERE id = " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.product_id LIMIT 1), " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name) AS \"name\" FROM " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblproducts ON " + Config.DATABASE_TABLE_PREFIX + "tblproducts.id = " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.product_id LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "tblcatagories ON " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id = " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.department WHERE " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.transaction_id = ? GROUP BY " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.product_id, " + Config.DATABASE_TABLE_PREFIX + "transactiontoproducts.price, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name");
			pstmt.setString(1, id);
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
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response);
	}
	private void logout(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		baseRequest.getSession().removeAttribute("user");
		successOut(response);
	}
	//TODO: This should be replaced with a stronger hashing function, try Apache commons Crypt
	public String hashPassword(String passwordToHash, String salt){
		//https://stackoverflow.com/questions/33085493/hash-a-password-with-sha-512-in-java	
		String generatedPassword = null;
		    try {
		         MessageDigest md = MessageDigest.getInstance("SHA-512");
		         md.update(salt.getBytes("UTF-8"));
		         byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
		         StringBuilder sb = new StringBuilder();
		         for(int i=0; i< bytes.length ;i++){
		            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		         }
		         generatedPassword = sb.toString();
		        } 
		       catch (NoSuchAlgorithmException e){
		        e.printStackTrace();
		       } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return generatedPassword;
		}
	private void login(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String email = baseRequest.getParameter("email");
		String password = baseRequest.getParameter("password");
		if ((email == null) || (password == null)) {
			errorOut(response, "missing parameters");
			return;
		}
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT id, name FROM " + Config.DATABASE_TABLE_PREFIX + "operators WHERE LCASE(email) = LCASE(?) AND passwordHash = ? LIMIT 1");
			pstmt.setString(1, email);
			pstmt.setString(2,  hashPassword(password, ""));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Log.log("User (" + rs.getString(1) + ") " + rs.getString(2) + " logged in successfully");
				successOut(response);
				return;
			}
			errorOut(response, "Incorrect Email/Password combination");
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}
	private void startTransaction(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pstmt = null;
		String operator = baseRequest.getParameter("cashier_id");
		if (operator == null) {
			errorOut(response, "missing fields");
			return;
		}
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("INSERT INTO " + Config.DATABASE_TABLE_PREFIX + "transactions (id, started, cashier) VALUES (?, ?, ?)");
			String guid = GUID();
			long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
			pstmt.setString(1, guid);
			pstmt.setLong(2, timestamp/1000);
			pstmt.setString(3, operator);
			if (pstmt.executeUpdate() > 0) {
				Log.log("Transaction (" + guid + ") STARTED by operator (" + operator + ")");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("success", true);
				jsonObject.put("id", guid);
				jsonObject.put("timeStart", timestamp/1000);
				response.getWriter().print(jsonObject);
				return;
			}
			errorOut(response, null);
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
	}
	private void getMessages(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String operator = baseRequest.getParameter("operator");
		String timeString = baseRequest.getParameter("time");
		if (operator == null || timeString == null) {
			errorOut(response, "missing fields");
			return;
		}
		int time = 0;
		try {
			time = Integer.parseInt(timeString);
		}
		catch (NumberFormatException e) {
			Log.log(e.toString());
			errorOut(response, "time not formatted correctly");
			return;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("(SELECT " + Config.DATABASE_TABLE_PREFIX + "tblchat.id,  " + Config.DATABASE_TABLE_PREFIX + "tblchat.sender AS \"senderId\", " + Config.DATABASE_TABLE_PREFIX + "tblchat.recipient as \"recipientId\", a.name AS \"senderName\", IFNULL(b.name, \"All\") AS recipientName, " + Config.DATABASE_TABLE_PREFIX + "tblchat.message, " + Config.DATABASE_TABLE_PREFIX + "tblchat.created FROM " + Config.DATABASE_TABLE_PREFIX + "tblchat LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "operators a ON a.id = " + Config.DATABASE_TABLE_PREFIX + "tblchat.sender LEFT JOIN " + Config.DATABASE_TABLE_PREFIX + "operators b ON b.id = " + Config.DATABASE_TABLE_PREFIX + "tblchat.recipient WHERE (" + Config.DATABASE_TABLE_PREFIX + "tblchat.recipient = ? OR " + Config.DATABASE_TABLE_PREFIX + "tblchat.sender = ? OR " + Config.DATABASE_TABLE_PREFIX + "tblchat.recipient = \"All\") AND " + Config.DATABASE_TABLE_PREFIX + "tblchat.created > ? ORDER BY " + Config.DATABASE_TABLE_PREFIX + "tblchat.created DESC LIMIT 20) ORDER BY created ASC");
			pstmt.setString(1, operator);
			pstmt.setString(2, operator);
			pstmt.setInt(3, time);
			rs = pstmt.executeQuery();
			JSONArray messages = new JSONArray();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("id",  rs.getString(1));
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
			response.getWriter().print(responseJson.toJSONString());
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}
	@SuppressWarnings("unchecked")
	public JSONObject getItemFromBarcode(String barcode) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblproducts.id, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.name, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.price, "
					+ "" + Config.DATABASE_TABLE_PREFIX + "tblproducts.barcode, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.department, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.labelPrinted, "
					+ "" + Config.DATABASE_TABLE_PREFIX + "tblproducts.isCase, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.units, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.unitType, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.updated, "
					+ "" + Config.DATABASE_TABLE_PREFIX + "tblproducts.created, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.deleted, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.status, " + Config.DATABASE_TABLE_PREFIX + "tblproducts.max_stock, "
					+ "" + Config.DATABASE_TABLE_PREFIX + "tblproducts.current_stock FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts WHERE barcode = ? LIMIT 1");
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
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
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
			pstmt = conn.prepareStatement("INSERT IGNORE INTO " + Config.DATABASE_TABLE_PREFIX + "tblproducts (id, barcode, name, created) VALUES (?, ?, ?, ?)");
			pstmt.setString(1, guid);
			pstmt.setString(2, barcode);
			pstmt.setString(3, name);
			pstmt.setLong(4, new Timestamp(System.currentTimeMillis()).getTime());
			if (pstmt.executeUpdate() > 0) {
				return guid;
			}
			return null;
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return null;
	}
	public void operatorLogin(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String code = baseRequest.getParameter("code");
		JSONObject json = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		if (code == null) {
			errorOut(response, "missing fields");
			return;
		}
		Log.log(code + "attemping to login");
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "operators.id, " + Config.DATABASE_TABLE_PREFIX + "operators.name FROM " + Config.DATABASE_TABLE_PREFIX + "operators WHERE code = ? AND deleted = 0 LIMIT 1");
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				json = new JSONObject();
				json.put("success", true);
				json.put("id", rs.getString(1));
				json.put("name", rs.getString(2));
				response.getWriter().print(json);
				Log.log("Operator " + rs.getString(2) + "(" + rs.getString(1) + ") logged in successfully");
				return;
			}
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		errorOut(response, null);
	}
	
	public void barcode(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String barcode = baseRequest.getParameter("number");
		baseRequest.getParameter("units");
		JSONObject product = null;
		if (barcode == null) {
			errorOut(response, "no barcode");
			return;
		}
		if (!barcode.matches("-?\\d+(\\.\\d+)?")) { //is numeric
			errorOut(response, "invalid barcode");
			return;
		}
		product = getItemFromBarcode(barcode);
		if (product != null) {
			response.getWriter().print(product.toJSONString());
			return;
		}
		insertProduct(barcode, "Unknown Product");
		product = new JSONObject();
		product = getItemFromBarcode(barcode);
		product.put("isNew", true);
		response.getWriter().print(product.toJSONString());
	}
	@SuppressWarnings("unchecked")
	public void getDepartments(HttpServletResponse response) throws IOException, ServletException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jsonArray = new JSONArray();
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.id, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.name, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.shorthand, " + Config.DATABASE_TABLE_PREFIX + "tblcatagories.colour FROM " + Config.DATABASE_TABLE_PREFIX + "tblcatagories WHERE deleted = 0 ORDER BY orderNum ASC");
			rs = pstmt.executeQuery();
			JSONObject jsonObject = null;
			while (rs.next()) {
				jsonObject = new JSONObject();
				jsonObject.put("id", rs.getString(1));
				jsonObject.put("name", rs.getString(2));
				jsonObject.put("shorthand", rs.getString(3));
				jsonObject.put("colour", rs.getString(4));
				jsonArray.add(jsonObject);
			}
			response.getWriter().print(jsonArray.toJSONString());
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
		finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
	}
	public void errorOut(HttpServletResponse response) throws IOException, ServletException {
		errorOut(response, null);
	}
	@SuppressWarnings("unchecked")
	public void errorOut(HttpServletResponse response, String reason) throws IOException, ServletException   {
		JSONObject json = new JSONObject();
		response.setStatus(200);
		json.put("success", false);
		if (reason != null) {
			json.put("reason", reason);
		}
		response.getWriter().print(json.toJSONString());
	}
	@SuppressWarnings("unchecked")
	public void successOut(HttpServletResponse response) throws IOException, ServletException   {
		JSONObject json = new JSONObject();
		json.put("success", true);
		response.setStatus(200);
		response.getWriter().print(json.toJSONString());
		
	}
}
