package com.opentill.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.opentill.database.SQLStatement;
import com.opentill.logging.Log;
import com.opentill.main.Config;

public class Importer {
	int maxImportLimit = 500;
	public Importer(File importFile) throws InvalidFormatException, IOException {
		this._construct(importFile, false);
	}
	public Importer(File importFile, boolean headerRow) throws IOException, InvalidFormatException {
		this._construct(importFile, headerRow);
	}
	private void _construct(File importFile, boolean headerRow) throws IOException, InvalidFormatException {
		String[] selectedColumns = new String[] {"Barcode", "Description"};

		if (!importFile.exists()) {
			throw new IOException(String.format("File does not exist (%s)", importFile.getAbsolutePath()));
		}
        InputStream inputStream = new FileInputStream(importFile);
        Workbook wb = WorkbookFactory.create(inputStream);
        int numberOfSheet = wb.getNumberOfSheets();
        for (int i = 0; i < numberOfSheet; i++) {
             Sheet sheet = wb.getSheetAt(i);
             Log.warn(sheet.getSheetName());
        }
        inputStream.close();

        // Getting the Sheet at index zero
        Sheet sheet = wb.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

        // 1. You can obtain a rowIterator and columnIterator and iterate over them
        System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
        Iterator<Row> rowIterator = sheet.rowIterator();
        int[] selectedColumnsIndex = new int[selectedColumns.length];
        if (headerRow) {
	        String[] columns = null;
	        if (rowIterator.hasNext()) {
	        	Row row = rowIterator.next();
	        	Iterator<Cell> cellIterator = row.cellIterator();
	        	columns = new String[row.getPhysicalNumberOfCells()];
	        	int i = 0;
	        	int index = 0;
	        	while (cellIterator.hasNext()) {
	                Cell cell = cellIterator.next();
	                columns[index] = cell.getStringCellValue();
	                for (String column:selectedColumns) {
	                	if (column.equals(cell.getStringCellValue())) {
	                		selectedColumnsIndex[i++] = index;
	                	}
	                }
	                index++;
	            }
	        }
        }
        ArrayList<String[]> rowsValue = new ArrayList<String[]>();
        int count = 0;
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String[] rowValue = new String[selectedColumnsIndex.length];
            // Now let's iterate over the columns of the current row
            int i = 0;
            count++;
            for (int index:selectedColumnsIndex) {
            	rowValue[i++] = (row.getCell(index).getStringCellValue());
            }
            rowsValue.add(rowValue);
            if (count == maxImportLimit) {
            	break;
            }
        }
        // Closing the workbook
        inputStream.close();
        try {
			Log.log(new SQLStatement().insertInto("kvs_tblproducts").columns(new String[] {"barcode", "name"}).values(rowsValue.toArray()).construct().toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public static void main(String[] args) {
		try {
			File file = new File(Config.APP_HOME + File.separatorChar + "import" + File.separatorChar + "BDPlof859530.xls");
			Importer importer = new Importer(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
