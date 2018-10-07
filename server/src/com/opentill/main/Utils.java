package com.opentill.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.validator.EmailValidator;
import org.eclipse.jetty.server.Request;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;

import uk.org.okapibarcode.backend.Code128;
import uk.org.okapibarcode.backend.HumanReadableAlignment;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.output.Java2DRenderer;

public class Utils {
	public static int SECONDS_IN_A_DAY = 86400;
	public static String GUID() {
		return UUID.randomUUID().toString();
	}

	public static String hashPassword(String passwordToHash, String salt) {
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt.getBytes("UTF-8"));
			byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generatedPassword;
	}
	
	public static Double round2(Double val) {
	    return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public static Long getCurrentTimeStamp() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		return cal.getTimeInMillis()/1000;
	}
	
	public static File generateBarcode(String barcodeContent) throws IOException {
		Code128 barcode = new Code128();
		barcode.setFontName("Helvetica");
		barcode.setFontSize(20);
		barcode.setModuleWidth(2);
		barcode.setBarHeight(barcode.getBarHeight());
		barcode.setHumanReadableAlignment(HumanReadableAlignment.RIGHT);
		barcode.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
		barcode.setContent(barcodeContent);

		int width = barcode.getWidth();
		int height = barcode.getHeight();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = image.createGraphics();
		Java2DRenderer renderer = new Java2DRenderer(g2d, 1, Color.WHITE, Color.BLACK);
		renderer.render(barcode);
		File file = new File(Config.APP_HOME + File.separatorChar + "temp" + File.separatorChar + "codabar.png");
		ImageIO.write(image, "png", file);
		return file;
	}

	public static String formatMoney(Float number) {
		if (number.isNaN()) {
			return null;
		}	
		NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.UK);
		return format.format(number.doubleValue());
	}
	
	public static String formatMoney(BigDecimal number) {
		NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.UK);
		return format.format(number);
	}
	
	public static String addTablePrefix(String sql) {
		return sql.replaceAll(":prefix:", Config.DATABASE_TABLE_PREFIX).replaceAll(":database_name:", Config.databaseProperties.getProperty("database_name"));
	}

	public static String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);  
	}
	
	public static boolean isNull(Object x) {
		return x == null;
	}

	public static boolean anyNulls(Object... params) {
		boolean ret = false;
		for (Object x : params) {
			if (x==null) {
				ret = true;
			}
		}
		return ret;
	}

	public static String getJSONFromRequest(Request request) {
		return request.getParameter(Config.JSON_PARAMETER_NAME);
	}

	public static boolean isValidEmail(String email) {
		return EmailAddressValidator.isValid(email);
	}
}
