package com.opentill.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opentill.main.Config;

public class ExcelHelper {
	public void testWrite() {
		//Workbook wb = new HSSFWorkbook();
	    XSSFWorkbook wb = new XSSFWorkbook();
	    XSSFCreationHelper createHelper = wb.getCreationHelper();
	    XSSFSheet sheet = wb.createSheet("hello world");

	    for (int i=0;i<300;i++) {
	    	// Create a row and put some cells in it. Rows are 0 based.
		    XSSFRow row = sheet.createRow(i);
		    // Or do it on one line.
		    row.createCell(0).setCellValue(i);
		    row.createCell(1).setCellValue(createHelper.createRichTextString("This is row" + i));
	    }
	    
	    // Write the output to a file
	    File file = new File(Config.APP_HOME + File.separatorChar + "report.xls");
	    FileOutputStream fileO;
		try {
			fileO = new FileOutputStream(file);
			wb.write(fileO);
			fileO.flush();
		    fileO.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createTakingsReport(HashMap<String, String> departmentsToNames, String[] departments, HashMap<String, HashMap<String, Double>> values, String stringJSONObject) {
		//Workbook wb = new HSSFWorkbook();
	    XSSFWorkbook wb = new XSSFWorkbook();
	    XSSFSheet sheet = wb.createSheet("Generated Report");
	    int rowId = 1;
	    int columnId = 1;
	    XSSFRow row = sheet.createRow(0);
	    for (String department : departments) {
	    	XSSFCell cell = row.createCell(columnId++);
	    	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
	    	cell.setCellValue(departmentsToNames.get(department));
	    }
	    for (Entry<String, HashMap<String, Double>> singleValue : values.entrySet()) {
	        String date = singleValue.getKey();
	        HashMap<String, Double> dayTotal = singleValue.getValue();
	        row = sheet.createRow(rowId++);
	        columnId = 0;
	        row.createCell(columnId++).setCellValue(date);
	    	for (String department : departments) {
	    		XSSFCell cell = row.createCell(columnId++);
	    		cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
	    		if (dayTotal.containsKey(department)) {
	    			cell.setCellValue(dayTotal.get(department));
	    		}
	    		else {
	    			cell.setCellValue(0);
	    		}
	    		
	    	}
	    }
	    // Write the output to a file
	    String filename = "TakingsReport.xls";
	    File file = new File(Config.APP_HOME + File.separatorChar + filename);
	    FileOutputStream fileO;
		try {
			fileO = new FileOutputStream(file);
			wb.write(fileO);
			fileO.flush();
		    fileO.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createProductLevelsReport(HashMap<String, String> departmentsToNames, String[] departments, HashMap<String, HashMap<String, Double>> values, String stringJSONObject) {
		//Workbook wb = new HSSFWorkbook();
	    XSSFWorkbook wb = new XSSFWorkbook();
	    XSSFSheet sheet = wb.createSheet("Generated Report");
	    int rowId = 1;
	    int columnId = 1;
	    XSSFRow row = sheet.createRow(0);
	    for (String department : departments) {
	    	XSSFCell cell = row.createCell(columnId++);
	    	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
	    	cell.setCellValue(departmentsToNames.get(department));
	    }
	    for (Entry<String, HashMap<String, Double>> singleValue : values.entrySet()) {
	        String date = singleValue.getKey();
	        HashMap<String, Double> dayTotal = singleValue.getValue();
	        row = sheet.createRow(rowId++);
	        columnId = 0;
	        row.createCell(columnId++).setCellValue(date);
	    	for (String department : departments) {
	    		XSSFCell cell = row.createCell(columnId++);
	    		cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
	    		if (dayTotal.containsKey(department)) {
	    			cell.setCellValue(dayTotal.get(department));
	    		}
	    		else {
	    			cell.setCellValue(0);
	    		}
	    		
	    	}
	    }
	    // Write the output to a file
	    String filename = "TakingsReport.xls";
	    File file = new File(Config.APP_HOME + File.separatorChar + filename);
	    FileOutputStream fileO;
		try {
			fileO = new FileOutputStream(file);
			wb.write(fileO);
			fileO.flush();
		    fileO.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
