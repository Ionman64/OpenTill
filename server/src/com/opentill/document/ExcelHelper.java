package com.opentill.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opentill.main.Config;
import com.opentill.products.Product;

public class ExcelHelper {
	public void testWrite() {
		// Workbook wb = new HSSFWorkbook();
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFCreationHelper createHelper = wb.getCreationHelper();
		XSSFSheet sheet = wb.createSheet("hello world");

		for (int i = 0; i < 300; i++) {
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

	public File createTakingsReport(HashMap<String, String> departmentsToNames, String[] departments,
			LinkedHashMap<String, LinkedHashMap<String, Double>> values) {
		// Workbook wb = new HSSFWorkbook();
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Takings");
		int rowId = 1;
		int columnId = 1;

		DataFormat datafrmt = wb.createDataFormat();
		Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
		XSSFCellStyle style3 = wb.createCellStyle();
		style3.setAlignment(CellStyle.ALIGN_RIGHT); // THIS LINE HERE!
		style3.setDataFormat(datafrmt.getFormat("�#,##0.00_);[Red](�#,##0.00)")); // Change for international
		styles.put("currency", style3);

		XSSFRow row = sheet.createRow(0);
		for (String department : departments) {
			XSSFCell cell = row.createCell(columnId++);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(departmentsToNames.get(department));
		}
		for (Entry<String, LinkedHashMap<String, Double>> singleValue : values.entrySet()) {
			String date = singleValue.getKey();
			LinkedHashMap<String, Double> dayTotal = singleValue.getValue();
			row = sheet.createRow(rowId++);
			columnId = 0;
			row.createCell(columnId++).setCellValue(date);
			for (String department : departments) {
				XSSFCell cell = row.createCell(columnId++);
				cell.setCellType(styles.get("currency").getIndex());
				if (dayTotal.containsKey(department)) {
					cell.setCellValue(dayTotal.get(department));
				} else {
					cell.setCellValue(0);
				}

			}
		}
		for (int i = 0; i < departments.length; i++) {
			sheet.autoSizeColumn(i);
		}
		try {
			// Write the output to a file
			String filename = UUID.randomUUID().toString() + ".xlsx";
			File file = new File(Config.APP_HOME + File.separatorChar + "temp" + File.separatorChar + filename);
			FileOutputStream fileO;
			fileO = new FileOutputStream(file);
			wb.write(fileO);
			fileO.flush();
			fileO.close();
			return file;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public File createProductLevelsReport(HashMap<String, String> departmentsToNames, String[] departments,
			HashMap<String, HashMap<String, Product>> values) {
		// Workbook wb = new HSSFWorkbook();
		XSSFWorkbook wb = new XSSFWorkbook();
		for (String department : departments) {
			if (department == null) {
				continue;
			}
			XSSFSheet sheet = wb.createSheet(departmentsToNames.get(department).replace("/", "-"));
			HashMap<String, Product> products = values.get(department);
			if (products == null) {
				continue;
			}
			int rowId = 0;
			int columnId = 0;
			XSSFRow row = sheet.createRow(rowId++);
			XSSFCell cell = row.createCell(columnId++);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("Name");

			cell = row.createCell(columnId++);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("Max Stock");

			cell = row.createCell(columnId++);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("Current Stock");

			cell = row.createCell(columnId++);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("Order Amount");

			for (Entry<String, Product> product : products.entrySet()) {
				Product tempProduct = product.getValue();
				if (tempProduct == null || tempProduct.name == null) {
					continue;
				}
				columnId = 0;

				row = sheet.createRow(rowId++);

				cell = row.createCell(columnId++);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(tempProduct.name);

				cell = row.createCell(columnId++);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(tempProduct.max_stock);

				cell = row.createCell(columnId++);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(tempProduct.current_stock);

				cell = row.createCell(columnId++);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				cell.setCellFormula("B" + rowId + "-C" + rowId + "");
			}
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
		}
		try {
			// Write the output to a file
			String filename = UUID.randomUUID().toString() + ".xlsx";
			File file = new File(Config.APP_HOME + File.separatorChar + "temp" + File.separatorChar + filename);
			FileOutputStream fileO;
			fileO = new FileOutputStream(file);
			wb.write(fileO);
			fileO.flush();
			fileO.close();
			return file;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
