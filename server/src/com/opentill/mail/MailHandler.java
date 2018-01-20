package com.opentill.mail;

import java.util.ArrayList;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.opentill.logging.Log;
import com.opentill.main.Config;

public class MailHandler {
	public static ArrayList<Email> emails = new ArrayList<Email>();
	public static boolean running = false;
	public static void run() {
		 Log.log("Mail Handler Started");
		 while (true) {
			 while (MailHandler.emails.size() > 0) {
				try {
					Email email = MailHandler.emails.get(0);
					email.setHostName(Config.emailProperties.getProperty("email_hostname"));
					email.setSmtpPort(Integer.parseInt(Config.emailProperties.getProperty("email_port")));
					email.setAuthenticator(new DefaultAuthenticator(Config.emailProperties.getProperty("email_user"), Config.emailProperties.getProperty("email_password")));
					email.setSSLOnConnect(true);
					email.setFrom(Config.emailProperties.getProperty("email_user"));
					email.send();
					MailHandler.emails.remove(0);
				} catch (EmailException e) {
					Log.log("Could not send email");
					e.printStackTrace();
				}
				catch (Exception e) {
					break;
				}
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ex) {
				Log.log(ex.toString());
				break;
			}
		 }
		 Log.log("Mail Handler has died");
	};
	public static void add(Email email) {
		// TODO Auto-generated method stub
		MailHandler.add(email);
	}
}
