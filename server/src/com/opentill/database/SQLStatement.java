package com.opentill.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import com.opentill.logging.Log;
import com.opentill.main.Utils;

public class SQLStatement {
	private int type = SQLFunction.UNKNOWN;
	private String databaseTable = null;
	private String[] affectedColumns = null;
	private String[] columnsPseudonyms = null;
	private Object[] values = null;
	private boolean multiInsert = false;
	private String[] groupByColumns = null;
	private String[] orderByColumns = null;
	private int limit = 0;
	private String sqlString = null;
	private String[] whereCase = null;
	
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		jo.put("type", this.type);
		jo.put("databaseTable", this.databaseTable);
		JSONArray joColumnsArray = new JSONArray();
		for (Object columns : this.affectedColumns) {
			joColumnsArray.add(columns);
		}
		jo.put("affectedColumns", joColumnsArray);
		jo.put("columnsPseudonyms", this.columnsPseudonyms);
		JSONArray joValuesArray = new JSONArray();
		for (Object value : this.values) {
			joValuesArray.add(value);
		}
		jo.put("values", joValuesArray);
		jo.put("groupByColumns", this.groupByColumns);
		jo.put("orderByColumns", this.orderByColumns);
		jo.put("limit", limit);
		jo.put("multiInsert", this.multiInsert);
		jo.put("whereCase", this.whereCase);
		return jo;
	}
	
	//Select
	public SQLStatement select(String[] columns) {
		this.type = SQLFunction.SELECT;
		this.affectedColumns = columns;
		return this;
	}
	
	public SQLStatement as(String[] columnsPseudonyms) {
		this.columnsPseudonyms = columnsPseudonyms;
		return this;
	}
	
	public SQLStatement from(String tableName) {
		this.databaseTable = tableName;
		return this;
	}
	
	public SQLStatement limit(int rowLimit) {
		this.limit = rowLimit;
		return this;
	}
	
	public SQLStatement where(String whereCase) {
		String[] tempWhereCase = new String[] {whereCase};
		this.whereCase = tempWhereCase;
		return this;
	}
	
	public SQLStatement where(String[] whereCase) {
		this.whereCase = whereCase;
		return this;
	}
	
	public SQLStatement orderBy(String[] orderBy) {
		this.orderByColumns = orderBy;
		return this;
	}
	
	public SQLStatement groupBy(String[] groupBy) {
		this.groupByColumns = groupBy;
		return this;
	}
	
	//Insert
	public SQLStatement insertInto(String tableName) {
		this.type = SQLFunction.INSERT;
		this.databaseTable = tableName;
		return this;
	}
	
	public SQLStatement columns(String[] columns) {
		this.affectedColumns = columns;
		return this;
	}
	
	/*Also Works for update */
	public SQLStatement values(Object[] values) {
		if ((values.length >= 1) && (values[0] instanceof Object[])) {
			this.multiInsert = true;
		}
		this.values = values;
		return this;
	}
	
	public SQLStatement update(String tableName) {
		this.type = SQLFunction.UPDATE;
		this.databaseTable = tableName;
		return this;
	}
	
	public SQLStatement deleteFrom(String tableName) {
		this.type = SQLFunction.DELETE;
		this.databaseTable = tableName;
		return this;
	}
	
	public SQLStatement alter(String tableName) {
		this.type = SQLFunction.ALTER;
		return this;
	}
	
	public ResultSet executeSelect() throws SQLException {
		if (this.sqlString == null) {
			throw new SQLException("Cannot run execute without constructing the sql statement first");
		}
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix(this.sqlString));
			int i = 0;
			for (Object value : this.values) {
				if (value.getClass().equals(int.class)) {
					pstmt.setInt(i++, (int) value);
			    }
				if (value.getClass().equals(Integer.class)) {
					pstmt.setInt(i++, (int) value);
			    }
			    if (value.getClass().equals(String.class)) {
			    	pstmt.setString(i++, (String) value);
			    }
				
			}
			rs = pstmt.executeQuery();
		} catch (Exception ex) {
			Log.error(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return rs;
	}
	
	public boolean executeUpdate() throws SQLException {
		if (this.sqlString == null) {
			throw new SQLException("Cannot run execute without constructing the sql statement first");
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseHandler.getDatabase();
			pstmt = conn.prepareStatement(Utils.addTablePrefix(this.sqlString));
			int i = 1;
			for (Object value : this.values) {
				if (value.getClass().equals(int.class)) {
					pstmt.setInt(i++, (int) value);
			    }
				if (value.getClass().equals(Integer.class)) {
					pstmt.setInt(i++, (int) value);
			    }
			    if (value.getClass().equals(String.class)) {
			    	pstmt.setString(i++, (String) value);
			    }
			    if (value.getClass().equals(Long.class)) {
			    	pstmt.setLong(i++, (Long) value);
			    }
			}
			if (pstmt.executeUpdate() > 0) {
				return true;
			}
		} catch (Exception ex) {
			Log.error(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(null, pstmt, conn);
		}
		return false;
	}
	
	public String toString() {
		return this.sqlString;
	}
	
	public SQLStatement construct() throws SQLException {
		switch (this.type) {
			case SQLFunction.ALTER:
				throw new SQLException("Not Implemented");
			case SQLFunction.DELETE:
				throw new SQLException("Not Implemented");
			case SQLFunction.INSERT:
				return insertConstruct();
			case SQLFunction.SELECT:
				return constructSelect();
			case SQLFunction.UPDATE:
				throw new SQLException("Not Implemented");
			case SQLFunction.UNKNOWN:
				throw new SQLException("SQL function not set (should be SELECT, INSERT, UPDATE, etc.)");
			default:
				throw new SQLException("SQL function not set (should be SELECT, INSERT, UPDATE, etc.)");
		}
	}
	
	private boolean isNull(Object x) {
		if (x == null) {
			return true;
		}
		return false;
	}
	
	private SQLStatement insertConstruct() throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		if (isNull(this.databaseTable)) {
			throw new SQLException("No database table set");
		}
		sql.append(this.databaseTable);
		
		if ((isNull(this.affectedColumns)) || (this.affectedColumns.length == 0)) {
			throw new SQLException("No columns specified for query");
		}
		StringJoiner sqlColumns = new StringJoiner(",", " (", ")");
		//Add Columns
		for (String column : this.affectedColumns) {
			sqlColumns.add(column);
		}
		
		sql.append(sqlColumns);
		sql.append(" VALUES ");
		
		if ((isNull(this.values)) || (this.values.length == 0)) {
			throw new SQLException("No values specified for query");
		}
		String insertSQL = new String();
		if (this.multiInsert) {
			StringJoiner multiInsertValues = new StringJoiner(",");
			for (Object row : this.values) {
				StringJoiner sqlValues = new StringJoiner(",", "(", ")");
				//Add Columns
				for (Object value : (Object[]) row) {
					sqlValues.add(value.toString());
				}
				multiInsertValues.add(sqlValues.toString());
			}
			insertSQL = multiInsertValues.toString();
		}
		else {
			StringJoiner sqlValues = new StringJoiner(",", " (", ")");
			//Add Columns
			for (Object value : this.values) {
				sqlValues.add("?");
			}
			insertSQL = sqlValues.toString();
		}
		sql.append(insertSQL);
		this.sqlString = sql.toString();
		return this;
	}
	
	private SQLStatement constructSelect() throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT");
		if (isNull(this.affectedColumns)) {
			throw new SQLException("No columns specified for query");
		}
		StringJoiner sqlColumns = new StringJoiner(",", " ", " ");
		//Add Columns
		if (isNull(this.columnsPseudonyms)) {
			for (String column : this.affectedColumns) {
				sqlColumns.add(column);
			}
		}
		else {
			if (this.columnsPseudonyms.length != this.affectedColumns.length) {
				throw new SQLException("Column Labels and Selected Columns must be the same length");
			}
			int index = 0;
			for (String column : this.affectedColumns) {
				if (this.columnsPseudonyms[index] == null) {
					sqlColumns.add(column);
				}
				else {
					sqlColumns.add(String.format("%s AS %s", column, this.columnsPseudonyms[index]));
				}
				index++;
			}
		}
		
		sql.append(sqlColumns);
		sql.append("FROM ");
		
		if (isNull(this.databaseTable)) {
			throw new SQLException("No database table set");
		}
		
		sql.append(this.databaseTable);
		
		if ((!isNull(this.groupByColumns)) && (this.groupByColumns.length != 0)) {
			StringJoiner columns = new StringJoiner(",");
			for (String column : this.groupByColumns) {
				columns.add(column);
			}
			sql.append(" GROUP BY ");
			sql.append(columns);
		}
		
		if ((!isNull(this.orderByColumns)) && (this.orderByColumns.length != 0)) {
			StringJoiner columns = new StringJoiner(",");
			for (String column : this.orderByColumns) {
				columns.add(column);
			}
			sql.append(" ORDER BY ");
			sql.append(columns);
		}
		
		if (this.limit > 0) {
			sql.append(" LIMIT " + this.limit);
		}
		this.sqlString = sql.toString();
		return this;
	}
	
	public static void main(String[] args) {
		try {
			new SQLStatement().insertInto(":prefix:tblproducts").columns(new String[] {"id", "barcode", "name"}).values(new Object[] {"a45", "1010101", "bacon"}).construct().executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
