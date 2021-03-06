package com.opentill.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.opentill.main.Config;
import com.opentill.models.ProductModel;

public class LabelExport {
	private final static String PLACEHOLDER_NAME = "{:NAME}";
	private final static String PLACEHOLDER_PRICE = "{:PRICE}";
	private final static String PLACEHOLDER_DATE = "{:DATE}";
	private final static String PLACEHOLDER_BARCODE = "{:BARCODE}";
	private final static String PLACEHOLDER_CHAR_LENGTH = "{:CHAR_LENGTH}";
	
	public static void main(String[] args) {
		ProductModel product = ProductModel.getProduct("005ae897-06e6-41ba-bda2-bdf4a109454a");
		try {
			LabelExport.generateLabel(product);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getTommorow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat format = new SimpleDateFormat("dd MMM");
		return format.format(calendar.getTime());
	}
	public static File generateLabel(ProductModel product) throws IOException {
		File file = new File(Config.APP_HOME + File.separatorChar + "temp" + File.separatorChar + product.id + ".lbx");
		
		//if (importTemplateLabelFile)

		String labelXMLStringWithDate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><pt:document xmlns:pt=\"http://schemas.brother.info/ptouch/2007/lbx/main\" xmlns:style=\"http://schemas.brother.info/ptouch/2007/lbx/style\" xmlns:text=\"http://schemas.brother.info/ptouch/2007/lbx/text\" xmlns:draw=\"http://schemas.brother.info/ptouch/2007/lbx/draw\" xmlns:image=\"http://schemas.brother.info/ptouch/2007/lbx/image\" xmlns:barcode=\"http://schemas.brother.info/ptouch/2007/lbx/barcode\" xmlns:database=\"http://schemas.brother.info/ptouch/2007/lbx/database\" xmlns:table=\"http://schemas.brother.info/ptouch/2007/lbx/table\" xmlns:cable=\"http://schemas.brother.info/ptouch/2007/lbx/cable\" version=\"1.5\" generator=\"P-touch Editor 5.2.001 Windows\"><pt:body currentSheet=\"Sheet 1\" direction=\"LTR\"><style:sheet name=\"Sheet 1\"><style:paper media=\"0\" width=\"175.7pt\" height=\"81.8pt\" marginLeft=\"4.3pt\" marginTop=\"8.4pt\" marginRight=\"4.4pt\" marginBottom=\"8.4pt\" orientation=\"portrait\" autoLength=\"false\" monochromeDisplay=\"true\" printColorDisplay=\"false\" printColorsID=\"0\" paperColor=\"#FFFFFF\" paperInk=\"#000000\" split=\"1\" format=\"274\" backgroundTheme=\"0\" printerID=\"14388\" printerName=\"Brother QL-800\"/><style:cutLine regularCut=\"0pt\" freeCut=\"\"/><style:backGround x=\"4.3pt\" y=\"8.4pt\" width=\"167.1pt\" height=\"65pt\" brushStyle=\"NULL\" brushId=\"0\" userPattern=\"NONE\" userPatternId=\"0\" color=\"#000000\" printColorNumber=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"/><pt:objects><table:table><pt:objectStyle x=\"4.3pt\" y=\"8.4pt\" width=\"167pt\" height=\"64.8pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"INSIDEFRAME\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"Table2\" ID=\"0\" lock=\"2\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><table:tableStyle row=\"3\" column=\"1\" autoSize=\"false\" keepSize=\"true\"/><table:gridPosition x=\"0pt 166.3pt\" y=\"0pt 21.4pt 42.8pt 64.1pt\"/><table:cells><table:cell addressX=\"1\" addressY=\"1\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><text:text><pt:objectStyle x=\"6pt\" y=\"10.1pt\" width=\"163.6pt\" height=\"18.7pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"NULL\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo><text:textControl control=\"FIXEDFRAME\" clipFrame=\"false\" aspectNormal=\"true\" shrink=\"true\" autoLF=\"true\" avoidImage=\"false\"/><text:textAlign horizontalAlignment=\"CENTER\" verticalAlignment=\"CENTER\" inLineAlignment=\"BASELINE\"/><text:textStyle vertical=\"false\" nullBlock=\"false\" charSpace=\"0\" lineSpace=\"0\" orgPoint=\"20pt\" combinedChars=\"true\"/><pt:data>{:NAME}</pt:data><text:stringItem charLen=\"{:CHAR_LENGTH}\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem></text:text><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell><table:cell addressX=\"1\" addressY=\"2\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><text:text><pt:objectStyle x=\"6pt\" y=\"31.5pt\" width=\"163.6pt\" height=\"18.7pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"NULL\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo><text:textControl control=\"FIXEDFRAME\" clipFrame=\"false\" aspectNormal=\"true\" shrink=\"true\" autoLF=\"true\" avoidImage=\"false\"/><text:textAlign horizontalAlignment=\"CENTER\" verticalAlignment=\"CENTER\" inLineAlignment=\"BASELINE\"/><text:textStyle vertical=\"false\" nullBlock=\"false\" charSpace=\"0\" lineSpace=\"0\" orgPoint=\"18.7pt\" combinedChars=\"false\"/><pt:data>{:PRICE} Use By {:DATE}</pt:data><text:stringItem charLen=\"1\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"1\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"3\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"11\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"2\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem></text:text><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell><table:cell addressX=\"1\" addressY=\"3\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><barcode:barcode><pt:objectStyle x=\"8pt\" y=\"52.9pt\" width=\"159.6pt\" height=\"18.6pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"INSIDEFRAME\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"Bar Code3\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><barcode:barcodeStyle protocol=\"CODE39\" lengths=\"48\" zeroFill=\"false\" barWidth=\"1.2pt\" barRatio=\"1:3\" humanReadable=\"true\" humanReadableAlignment=\"LEFT\" checkDigit=\"false\" autoLengths=\"true\" margin=\"true\" sameLengthBar=\"false\" bearerBar=\"false\"/><pt:data>{:BARCODE}</pt:data></barcode:barcode><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell></table:cells></table:table></pt:objects></style:sheet></pt:body></pt:document>";
		String propXMLString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><meta:properties xmlns:meta=\"http://schemas.brother.info/ptouch/2007/lbx/meta\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\"><meta:appName>P-touch Editor</meta:appName><dc:title></dc:title><dc:subject></dc:subject><dc:creator>pete</dc:creator><meta:keyword></meta:keyword><dc:description></dc:description><meta:template></meta:template><dcterms:created>2018-01-25T12:24:21Z</dcterms:created><dcterms:modified>2018-09-06T16:19:22Z</dcterms:modified><meta:lastPrinted>2018-01-25T12:25:38Z</meta:lastPrinted><meta:modifiedBy>pete</meta:modifiedBy><meta:revision>2</meta:revision><meta:editTime>2</meta:editTime><meta:numPages>1</meta:numPages><meta:numWords>0</meta:numWords><meta:numChars>0</meta:numChars><meta:security>0</meta:security></meta:properties>";

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
		
		//label.xml
		ZipEntry label = new ZipEntry("label.xml");
		out.putNextEntry(label);
		
		labelXMLStringWithDate = labelXMLStringWithDate.replace(PLACEHOLDER_NAME, product.name).replace(PLACEHOLDER_BARCODE, product.barcode).replace(PLACEHOLDER_PRICE, product.price.toString()).replace(PLACEHOLDER_DATE, getTommorow()).replace(PLACEHOLDER_CHAR_LENGTH, Integer.toString(product.name.length()));
		
		
		byte[] data = labelXMLStringWithDate.toString().getBytes();
		out.write(data, 0, data.length);
		out.closeEntry();
		
		//prop.xml
		ZipEntry prop = new ZipEntry("prop.xml");
		out.putNextEntry(prop);
		data = propXMLString.toString().getBytes();
		out.write(data, 0, data.length);
		out.closeEntry();

		out.close();
		return file;
	}
}
