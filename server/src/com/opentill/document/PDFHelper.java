package com.opentill.document;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.opentill.main.Config;

public class PDFHelper {
	public static void drawTable(PDPage page, PDPageContentStream contentStream, float margin, float y, String[][] content) throws IOException {
		final int rows = content.length;
		final int cols = content[0].length;
		final float rowHeight = 20f;
		final float tableWidth = page.getMediaBox().getWidth()-(2*margin);
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth/(float)cols;
		final float cellMargin=5f;

		//draw the rows
		float nexty = y ;
		for (int i = 0; i <= rows; i++) {
			contentStream.moveTo(margin,nexty);
			contentStream.lineTo(margin+tableWidth,nexty);
			contentStream.stroke();
			nexty-= rowHeight;
		}

		//draw the columns
		float nextx = margin;
		for (int i = 0; i <= cols; i++) {
			contentStream.moveTo(nextx, y);
			contentStream.lineTo(nextx,y-tableHeight);
			contentStream.stroke();
			nextx += colWidth;
		}

		//now add the text
		contentStream.setFont(PDType1Font.HELVETICA_BOLD,12);

		float textx = margin+cellMargin;
		float texty = y-15;
		for(int i = 0; i < content.length; i++){
			for(int j = 0 ; j < content[i].length; j++){
				String text = content[i][j];
				contentStream.beginText();
				contentStream.newLineAtOffset(textx, texty);
				contentStream.showText(text);
				contentStream.endText();
				textx += colWidth;
			}
			texty-=rowHeight;
			textx = margin+cellMargin;
		}
	}

	public static void writeReportHeader(PDPage page, PDPageContentStream contentStream, float x, float y, String title, String subtitle, String document_number) throws IOException {
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
		contentStream.beginText();
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(title);
		contentStream.endText();
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
		contentStream.beginText();
		contentStream.newLineAtOffset(x, y-30);
		contentStream.showText(subtitle);
		contentStream.endText();
		
	}

	public static void createPDF() {
		final float page_margin = 25;
		PDDocument doc = new PDDocument();
		String filename = Config.APP_HOME + File.separatorChar + "example.pdf";
		PDPage page = new PDPage();
		doc.addPage(page);

		try 
		{
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			
			PDFHelper.writeReportHeader(page, contentStream, page_margin, 750F, "Report Header", "Example Report Header", "");

			String[][] content = {{"Staff","Something", "Off", "Ken", "Bob", "Cheese", "Barberra", "ben", "Korn", "Bacon"},
					{"1","2", "3", "4", "5", "6", "7", "8", "9", "10"},
					{"1","2", "3", "4", "5", "6", "7", "8", "9", "10"},
					{"1","2", "3", "4", "5", "6", "7", "8", "9", "10"},
					{"1","2", "3", "4", "5", "6", "7", "8", "9", "10"}} ;
			PDFHelper.drawTable(page, contentStream, page_margin, 700, content);
			contentStream.close();
			doc.save(filename);
			doc.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}


	}
}
