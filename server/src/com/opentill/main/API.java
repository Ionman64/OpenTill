package com.opentill.main;

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
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.opentill.logging.Log;
import com.opentill.database.DatabaseHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.eclipse.jetty.server.Request;

public class API extends ContextHandler
{
	public API (String context) {
		super.setContextPath(context);
		super.setAllowNullPathInfo(true); ///Allows Post
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setContentType("application/json; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		switch (baseRequest.getParameter("function")) {
			case "BARCODE":
				barcode(baseRequest, response);
				break;
			case "OPERATORLOGON":
				operatorLogin(baseRequest, response);
				break;
			case "TRANSACTION":
				startTransaction(baseRequest, response);
				break;
			case "DEPARTMENTS":
				getDepartments(response);
				break;
			case "GETALLSUPPLIERS":
				break;
			case "GETMESSAGES":
				getMessages(baseRequest, response);
				break;
			default:
				errorOut(response, "No such function");
		}
	    // Inform jetty that this request has now been handled
	    baseRequest.setHandled(true);
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
			pstmt = conn.prepareStatement("INSERT INTO kvs_transactions (id, started, cashier) VALUES (?, ?, ?)");
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
			closeDBResources(null, pstmt, conn);
		}
	}
	private void getMessages(Request baseRequest, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("success", true);
		json.put("messages", new JSONArray());
		response.getWriter().print(json.toJSONString());
	}
	@SuppressWarnings("unchecked")
	public JSONObject getItemFromBarcode(String barcode) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement("SELECT kvs_tblproducts.id, kvs_tblproducts.name, kvs_tblproducts.price, "
					+ "kvs_tblproducts.barcode, kvs_tblproducts.department, kvs_tblproducts.labelPrinted, "
					+ "kvs_tblproducts.isCase, kvs_tblproducts.units, kvs_tblproducts.unitType, kvs_tblproducts.updated, "
					+ "kvs_tblproducts.created, kvs_tblproducts.deleted, kvs_tblproducts.status, kvs_tblproducts.max_stock, "
					+ "kvs_tblproducts.current_stock FROM kvs_tblproducts WHERE barcode = ? LIMIT 1");
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
			closeDBResources(rs, pstmt, conn);
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
			pstmt = conn.prepareStatement("INSERT IGNORE INTO kvs_tblproducts (id, barcode, name, created) VALUES (?, ?, ?, ?)");
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
			closeDBResources(null, pstmt, conn);
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
			pstmt = conn.prepareStatement("SELECT kvs_operators.id, kvs_operators.name FROM kvs_operators WHERE code = ? AND deleted = 0 LIMIT 1");
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
			closeDBResources(rs, pstmt, conn);
		}
		errorOut(response, null);
	}
	public void barcode(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		String barcode = baseRequest.getParameter("number");
		String units = baseRequest.getParameter("units");
		JSONObject product = null;
		if (barcode == null) {
			errorOut(response, "no barcode");
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
			pstmt = conn.prepareStatement("SELECT kvs_tblcatagories.id, kvs_tblcatagories.name, kvs_tblcatagories.shorthand, kvs_tblcatagories.colour FROM kvs_tblcatagories WHERE deleted = 0 ORDER BY orderNum ASC");
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
			closeDBResources(rs, pstmt, conn);
		}
	}
	@SuppressWarnings("unchecked")
	public void errorOut(HttpServletResponse response, String reason) throws IOException, ServletException   {
		JSONObject json = new JSONObject();
		json.put("success", false);
		if (reason != null) {
			json.put("reason", reason);
		}
		response.getWriter().print(json.toJSONString());
	}
	public void closeDBResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
			try {
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
				if (pstmt != null && !pstmt.isClosed()) {
					pstmt.close();
				}
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
