package com.opentill.main;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.UUID;

import com.opentill.logging.Log;

public class Utils {
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

	public static Long getCurrentTimeStamp() {
		// TODO Auto-generated method stub
		return System.currentTimeMillis();
	}

	public static String formatMoney(Float number) {
		if (number.isNaN()) {
			return null;
		}	
		NumberFormat format = NumberFormat.getCurrencyInstance(java.util.Locale.UK);
		return format.format(number.doubleValue());
	}
}
