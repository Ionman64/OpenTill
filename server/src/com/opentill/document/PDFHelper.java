package com.opentill.document;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.main.Utils;
import com.opentill.products.LabelStyle;
import com.opentill.products.OrderProduct;
import com.opentill.products.Product;

public class PDFHelper {
	public static String repeat(int count, String with) {
		String newString = new String(new char[count]).replace("\0", with);
		return newString.substring(0, newString.length()-1);
	}
	public static ArrayList<Product> getLabels(String[] ids, boolean includeLabelled) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Product> products = new ArrayList<Product>();
		try {
			conn = DatabaseHandler.getDatabase();
			String sql;
			if (!includeLabelled) {
				sql = "SELECT name, price, barcode FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts WHERE id IN (:?:) GROUP BY id";
			}
			else {
				sql = "SELECT name, price, barcode FROM " + Config.DATABASE_TABLE_PREFIX + "tblproducts WHERE labelPrinted = 1 OR id IN (:?:) GROUP BY id";
			}
			sql = sql.replace(":?:",  repeat(ids.length, "?,"));
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			for (String id : ids) {
				pstmt.setString(i++, id);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.name = rs.getString(1);
				product.price = rs.getFloat(2);
				product.barcode = rs.getString(3);
				products.add(product);
			}
		} catch (SQLException ex) {
			Log.error(ex.toString());
		} finally {
			DatabaseHandler.closeDBResources(rs, pstmt, conn);
		}
		return products;
	}
	
	public static void drawLabels(PDDocument doc, float margin, String[] products, boolean includeLabelled) throws IOException {
		//Float currentY = 675f;//page.getMediaBox().getHeight() - margin; // (2 * margin);
		LabelStyle labelStyle = new LabelStyle();
		
		PDPage page = null;
		
		float pageXLimit = 0F; 
		float pageYLimit = 0F;

		float currentX = 0F;
		float currentY = 0F;
		
		float pageWidth = 0F;
		PDPageContentStream contentStream = null;
		
		boolean newPageNeeded = true;
		Iterator<Product> iter = getLabels(products, includeLabelled).iterator();
		if (!iter.hasNext()) {
			page = new PDPage();
			doc.addPage(page);
			currentX = page.getCropBox().getLowerLeftX() + margin;
			currentY = page.getCropBox().getUpperRightY() - margin;
			contentStream = new PDPageContentStream(doc, page);
			contentStream.moveTo(currentX, currentY);
			PDFont helvetica = PDType1Font.HELVETICA;
			int fontSize = 46;
			contentStream.setFont(helvetica, fontSize);
			contentStream.beginText();
			contentStream.showText("No Labels");
			contentStream.endText();
			contentStream.close();
			return;
		}
		while (iter.hasNext()) {
			Product tempProduct = iter.next();
			if ((newPageNeeded) || (currentY - (labelStyle.getHeight() + margin) < 0)) {
				if (contentStream != null) {
					contentStream.close();
				}
				if (page != null) {
					doc.addPage(page);
				}
				page = new PDPage();
				
				contentStream = new PDPageContentStream(doc, page);
				contentStream.setLineWidth(labelStyle.getBorderWidth());

				currentX = page.getCropBox().getLowerLeftX() + margin;
				currentY = page.getCropBox().getUpperRightY() - margin;

				pageXLimit = page.getCropBox().getWidth();
				pageYLimit = page.getCropBox().getHeight();
				
				pageWidth = page.getMediaBox().getWidth() - (2 * margin);
				newPageNeeded = false;
			}
			
			contentStream.moveTo(currentX, currentY);
			PDFHelper.writeLabelInner(doc, page, contentStream, currentX+labelStyle.getPadding(), currentY - labelStyle.getPadding()*2, tempProduct.name,tempProduct.price, labelStyle);
			contentStream.addRect(currentX, currentY - labelStyle.getHeight(), labelStyle.getWidth(), labelStyle.getHeight());
			contentStream.setStrokingColor(labelStyle.getColorRed(), labelStyle.getColorGreen(), labelStyle.getColorBlue());
			contentStream.stroke();
			
			currentX = currentX + labelStyle.getWidth() + margin;
			if ((currentX + labelStyle.getWidth()) > pageXLimit) {
				currentX = margin;
				currentY = currentY - (labelStyle.getHeight() + margin);
			}
		}
		if (contentStream != null) {
			contentStream.close();
		}
		if (page != null) {
			doc.addPage(page);
		}
	}
	public static void drawTable(PDPage page, PDPageContentStream contentStream, float margin, float y,
			String[][] content) throws IOException {
		final int rows = content.length;
		final int cols = content[0].length;
		PDFont font = PDType1Font.HELVETICA_BOLD;
		contentStream.setFont(font, 16);
		final float rowHeight = 30f;
		final float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth / 5F;
		final float cellMargin = 5f;

		// draw the rows
		float nexty = y;
		for (int i = 0; i <= rows; i++) {
			contentStream.moveTo(margin, nexty);
			contentStream.lineTo(margin + tableWidth, nexty);
			contentStream.stroke();
			nexty -= rowHeight;
		}

		// draw the columns
		float nextx = margin;
		for (int i = 0; i <= cols; i++) {
			contentStream.moveTo(nextx, y);
			contentStream.lineTo(nextx, y - tableHeight);
			contentStream.stroke();
			if (i == 0) {
				nextx += colWidth;
			}
			else {
				nextx += colWidth*4;
			}
		}

		float textx = margin + cellMargin;
		float texty = y - rowHeight/2 - 5F;
		for (int i = 0; i < content.length; i++) {
			for (int j = 0; j < content[i].length; j++) {
				String text = content[i][j];
				contentStream.beginText();
				contentStream.newLineAtOffset(textx, texty);
				contentStream.showText(text);
				contentStream.endText();
				if (j == 0) {
					textx += colWidth;
				}
				else {
					textx += colWidth*4;
				}
			}
			texty -= rowHeight;
			textx = margin + cellMargin;
		}
	}
	
	public static void writeLabelInner(PDDocument doc, PDPage page, PDPageContentStream contentStream, float x, float y, String title,
			float price, LabelStyle labelStyle) throws IOException {
		PDFont helvetica = PDType1Font.HELVETICA;
		int fontSize = 16;
		float titleWidth = helvetica.getStringWidth(title) / 1000 * fontSize;
		contentStream.setFont(helvetica, fontSize);
		contentStream.beginText();
		
		String strippedString = title;
		while ((helvetica.getStringWidth(strippedString) / 1000 * fontSize) > labelStyle.width) {
			strippedString = strippedString.substring(0, strippedString.length()-1);
		}
		if (!strippedString.equals(title)) {
			strippedString = strippedString.substring(0, strippedString.length()-3).concat("...");
		}
		contentStream.newLineAtOffset(x + ((labelStyle.getWidth() - (helvetica.getStringWidth(strippedString) / 1000 * fontSize) - labelStyle.getPadding()) / 2), y);
		contentStream.showText(strippedString);
		contentStream.endText();
		
		//Price
		String stringPrice = Utils.formatMoney(price);
		File fontFile = new File("/usr/share/fonts/truetype/ubuntu/Ubuntu-M.ttf");
		PDFont font =  PDType0Font.load(doc, fontFile);
		int fontSize2 = 60;
		float width = 0F;
		while ((width = font.getStringWidth(stringPrice) / 1000 * fontSize2) > labelStyle.width) {
			fontSize2--;
		}
		contentStream.setFont(font, fontSize2);
		contentStream.beginText();
		contentStream.newLineAtOffset(x + ((labelStyle.getWidth() - width) / 2) - labelStyle.getPadding(), y-50);
		contentStream.showText(stringPrice);
		contentStream.endText();

	}

	public static void writeReportHeader(PDPage page, PDPageContentStream contentStream, float margin, String title,
			String subtitle, String document_number) throws IOException {
		
		float currentY = page.getMediaBox().getUpperRightY() - margin - 20;
		float currentX = page.getMediaBox().getLowerLeftX() + margin;
		PDFont font = PDType1Font.HELVETICA_BOLD;
		contentStream.setFont(font, 24);
		contentStream.beginText();
		contentStream.newLineAtOffset(currentX, currentY);
		contentStream.showText(title);
		contentStream.endText();
		
		contentStream.setFont(font, 14);
		contentStream.beginText();
		contentStream.newLineAtOffset(currentX, currentY-30);
		contentStream.showText(subtitle);
		contentStream.endText();

	}
	
	public static String createLabelsPDF(String[] products, boolean includeLabelled) {
		final float page_margin = 25;
		PDDocument doc = new PDDocument();
		String guid = Utils.GUID();
		String filename = Config.APP_HOME + File.separatorChar + "temp" + File.separatorChar + guid + ".pdf";
		try {
			PDFHelper.drawLabels(doc, page_margin, products, includeLabelled);
			doc.save(filename);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "temp" + File.separatorChar + guid + ".pdf";
	}
	
	public static String createOrderSheet(String orderId, String supplierName, ArrayList<OrderProduct> products) {
		final float page_margin = 15;
		int pageNum = 1;
		String guid = Utils.GUID();
		String filename = Config.APP_HOME + File.separatorChar + "temp" + File.separatorChar + guid + ".pdf";
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);
		String[][] array = new String[products.size()+1][2];
		array[0][0] = "Quantity";
		array[0][1] = "Product";
		Iterator<OrderProduct> iter = products.iterator();
		int i = 1;
		while (iter.hasNext()) {
			OrderProduct orderProduct = iter.next();
			array[i][0] = Integer.toString(orderProduct.order_stock);
			array[i++][1] = orderProduct.name;
		}
		try {
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.setLineWidth(2.0F);
			PDFHelper.writeReportHeader(page, contentStream, page_margin, String.format("%s Order", supplierName), String.format("%s | %s | %s", "Knutton Village Store", Utils.getCurrentDate(), "Page " + pageNum), "1");
			PDImageXObject pdImage = PDImageXObject.createFromFileByContent(Utils.generateBarcode(String.format("[Order:%s]", orderId)), doc);

			PDFHelper.drawTable(page, contentStream, page_margin, page.getMediaBox().getUpperRightY()-100, array);
			float imageHeight = 50;
			float imageWidth = 300;
			
			contentStream.drawImage(pdImage, page.getMediaBox().getUpperRightX() - page_margin - imageWidth, page.getMediaBox().getUpperRightY() - page_margin - imageHeight, imageWidth, imageHeight);
			contentStream.close();
			doc.save(filename);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "temp" + File.separatorChar + guid + ".pdf";
	}
	
	public static void createPDF() {
		final float page_margin = 25;
		PDDocument doc = new PDDocument();
		String filename = Config.APP_HOME + File.separatorChar + "example.pdf";
		try {
			//PDFHelper.writeReportHeader(page, contentStream, page_margin, 750F, "Report Header","Example Report Header", "");
			
			//PDFHelper.drawLabels(doc, page_margin);
			
			doc.save(filename);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public float pt2mmForWeb72dpi(float pt) {
	   return pt2mm(pt,72);
	}
	public float pt2mmForPrint300dpi(float pt) {
	   return pt2mm(pt,300);
	}
	public float pt2mmForPrint600dpi(float pt) {
	   return pt2mm(pt,600);
	}
	public float pt2mm(float pt, float dpi) {
	   return pt * 25.4f / dpi;
	}
}
