package com.opentill.mail;

import java.util.ArrayList;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import com.opentill.logging.Log;

public class MailHandler {
	public static ArrayList<Email> emails = new ArrayList<Email>();
	public static boolean interuptted = false;
	public static void run() {
		Log.info("Started: Mail Handler");
		while (!interuptted) {
			while (MailHandler.emails.size() > 0) {
				try {
					Email email = MailHandler.emails.remove(0);
					
					Mailer mailer = MailerBuilder
					          .withSMTPServer("127.0.0.1", 1025)
					          .withSessionTimeout(10 * 1000)
					          .buildMailer();
					
					/*Mailer mailer = MailerBuilder
					          .withSMTPServer(Config.emailProperties.getProperty("email_hostname"), Integer.parseInt(Config.emailProperties.getProperty("email_port")), Config.emailProperties.getProperty("email_user"), Config.emailProperties.getProperty("email_password"))
					          .withTransportStrategy(TransportStrategy.SMTP_TLS)
					          .trustingSSLHosts(Config.emailProperties.getProperty("email_hostname"))
					          .withSessionTimeout(10 * 1000)
					          .buildMailer();
					*/
					mailer.sendMail(email);
				} 
				catch (Exception e) {
					interuptted = true;
					break;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				interuptted = true;
				e.printStackTrace();
			}
		}
		Log.info("Mail Handler has died");
	};

	public static void add(Email email) {
		// TODO Auto-generated method stub
		MailHandler.emails.add(email);
	}
}
