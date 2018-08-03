package com.opentill.document;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.poi.ss.usermodel.Color;

import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.products.LabelStyle;

public class PDFHelper {
	public static void drawLabels(PDDocument doc, float margin) throws IOException {
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

		for (int i=0;i<50;i++) {
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
			PDFHelper.writeLabelInner(doc, page, contentStream, currentX+labelStyle.getPadding(), currentY - labelStyle.getPadding()*2, "Product Name For make Glorious Nation of Kazakstan","£" + i + ".00", labelStyle);
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
		final float rowHeight = 20f;
		final float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth / cols;
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
			nextx += colWidth;
		}

		// now add the text
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

		float textx = margin + cellMargin;
		float texty = y - 15;
		for (int i = 0; i < content.length; i++) {
			for (int j = 0; j < content[i].length; j++) {
				String text = content[i][j];
				contentStream.beginText();
				contentStream.newLineAtOffset(textx, texty);
				contentStream.showText(text);
				contentStream.endText();
				textx += colWidth;
			}
			texty -= rowHeight;
			textx = margin + cellMargin;
		}
	}
	
	public static void writeLabelInner(PDDocument doc, PDPage page, PDPageContentStream contentStream, float x, float y, String title,
			String subtitle, LabelStyle labelStyle) throws IOException {
		PDFont helvetica = PDType1Font.HELVETICA;
		int fontSize = 16;
		float titleWidth = helvetica.getStringWidth(title) / 1000 * fontSize;
		contentStream.setFont(helvetica, fontSize);
		contentStream.beginText();
		
		String strippedString = title;
		while ((helvetica.getStringWidth(strippedString) / 1000 * fontSize) > labelStyle.width) {
			strippedString = strippedString.substring(0, strippedString.length()-1);
			Log.log("Stripping");
		}
		if (!strippedString.equals(title)) {
			strippedString = strippedString.substring(0, strippedString.length()-3).concat("...");
		}
		contentStream.newLineAtOffset(x + ((labelStyle.getWidth() - (helvetica.getStringWidth(strippedString) / 1000 * fontSize) - labelStyle.getPadding()) / 2), y);
		contentStream.showText(strippedString);
		contentStream.endText();
		
		//Price
		File fontFile = new File("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf");
		PDFont font =  PDType0Font.load(doc, fontFile);
		int fontSize2 = 60;
		float width = 0F;
		while ((width = font.getStringWidth(subtitle) / 1000 * fontSize2) > labelStyle.width) {
			fontSize2--;
		}
		contentStream.setFont(font, fontSize2);
		contentStream.beginText();
		contentStream.newLineAtOffset(x + ((labelStyle.getWidth() - width) / 2) - labelStyle.getPadding(), y-50);
		contentStream.showText(subtitle);
		contentStream.endText();

	}

	public static void writeReportHeader(PDPage page, PDPageContentStream contentStream, float x, float y, String title,
			String subtitle, String document_number) throws IOException {
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
		contentStream.beginText();
		contentStream.moveTo(x, y);
		contentStream.showText(title);
		contentStream.endText();
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
		contentStream.beginText();
		contentStream.moveTo(x, y - 75);
		contentStream.showText(subtitle);
		contentStream.endText();

	}
	public static void createPDF() {
		final float page_margin = 25;
		PDDocument doc = new PDDocument();
		String filename = Config.APP_HOME + File.separatorChar + "example.pdf";
		try {
			

			//PDFHelper.writeReportHeader(page, contentStream, page_margin, 750F, "Report Header","Example Report Header", "");

			String[][] content = {
					{ "Staff", "Something", "Off", "Ken", "Bob", "Cheese", "Barberra", "ben", "Korn", "Bacon" },
					{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" },
					{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" },
					{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" },
					{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" } };
			PDFHelper.drawLabels(doc, page_margin);
			
			doc.save(filename);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
