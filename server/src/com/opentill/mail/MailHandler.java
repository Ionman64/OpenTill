package com.opentill.mail;

import java.util.ArrayList;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.opentill.logging.Log;

public class MailHandler implements Runnable {
	private ArrayList<EmailMessage> emails = new ArrayList<EmailMessage>();
	private String handlerMailAddress = null;
	private String handlerMailPassword = null;
	public MailHandler(String handlerMailAddress, String handlerMailPassword) {
		this.handlerMailAddress = handlerMailAddress;
		this.handlerMailPassword = handlerMailPassword;
	}
	public void addMail(EmailMessage email) {
		this.emails.add(email);
	}
	@Override
	public void run() {
		while (true) {
			if (this.emails.size() > 0) {
				try {
					EmailMessage emailMessage = this.emails.get(0);
					Email email = new SimpleEmail();
					email.setHostName("smtp.googlemail.com");
					email.setSmtpPort(465);
					email.setAuthenticator(new DefaultAuthenticator(this.handlerMailAddress, this.handlerMailPassword));
					email.setSSLOnConnect(true);
					email.setFrom(this.handlerMailAddress);
					email.setSubject(emailMessage.getSubject());
					email.setMsg(emailMessage.getBody());
					for (String to : emailMessage.getRecipients()) {
						email.addTo(to);
					}
					email.send();
					this.emails.remove(0);
					
				} catch (EmailException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				Log.log("MailHandler has failed to sleep and has shut down");
				break;
			}
		}
	}
}
