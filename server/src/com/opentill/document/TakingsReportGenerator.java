package com.opentill.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opentill.main.Config;

public class TakingsReportGenerator {
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
}
