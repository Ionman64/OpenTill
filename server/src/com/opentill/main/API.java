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
import org.eclipse.jetty.server.handler.AbstractHandler;
//import org.omg.CORBA.Request;

import com.opentill.logging.Log;
import com.opentill.database.DatabaseHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.jetty.server.Request;

public class API extends AbstractHandler
{
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Log.log(baseRequest.getParameter("function"));
		JSONObject json = new JSONObject();
		response.setContentType("application/json; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		switch (baseRequest.getParameter("function")) {
			case "BARCODE":
				barcode(response);
				break;
			case "DEPARTMENTS":
				getDepartments(response);
				break;
			case "GETALLSUPPLIERS":
				break;
			default:
				errorOut(response, "No such function");
		}
	    // Inform jetty that this request has now been handled
	    baseRequest.setHandled(true);
	}
	public void barcode(Request baseRequest, HttpServletResponse response) throws IOException, ServletException {
		JSONObject json = new JSONObject();
		String barcode = baseRequest.getParameter("number");
		String units = baseRequest.getParameter("units");
		String rs = null;
		if (barcode == null) {
			errorOut(response, "no barcode");
		}
		if (rs = get_item_from_barcode(barcode)) {
			die (json_encode(rs));
		}
		if (get_item_from_UPC(barcode)) {
			die (json_encode(get_item_from_barcode(barcode)));
		}
		insert_product(barcode);
		json = get_item_from_barcode(barcode);
		json.put("isNew", true);
		response.getWriter().println(json.toJSONString());
	}

	private String get_item_from_barcode(String barcode) {
		// TODO Auto-generated method stub
		return null;
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
			response.getWriter().println(jsonArray.toJSONString());
			pstmt.close();
			conn.close();
		}
		catch (SQLException ex) {
			Log.log(ex.toString());
		}
	}
	public void errorOut(HttpServletResponse response, String reason) throws IOException, ServletException   {
		JSONObject json = new JSONObject();
		json.put("success", false);
		if (reason != null) {
			json.put("reason", reason);
		}
		response.getWriter().println(json.toJSONString());
	}
}
