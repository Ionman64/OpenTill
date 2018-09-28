package com.opentill.mail;


import org.apache.commons.mail.EmailException;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;

import com.opentill.main.Config;
import com.opentill.main.Utils;

public class PasswordResetEmail {
	public String name;
	public String respondentEmail;
	public PasswordResetEmail(String name, String respondentEmail) {
		this.name = name;
		this.respondentEmail = respondentEmail;
	}
	public String getSubject() {
		return String.format("[%s] You Have Reset Your Password", Config.APP_NAME);
	}
	public Email toEmail() throws EmailException {
		return EmailBuilder.startingBlank()
			    .from("OpenTill Robot", Config.emailProperties.getProperty("email_user"))
			    .to("Peter", this.respondentEmail)
			    .withSubject(this.getSubject())
			    .withHTMLText(this.getBody())
			    .buildEmail();
	}
	public String getBody() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Hello %s,", this.name));
		sb.append("</br>");
		sb.append("</br>");
		sb.append(String.format("You have reset your password at %s, if this was in error please contact your supervisor", Utils.getCurrentDate()));
		sb.append("</br>");
		sb.append("</br>");
		sb.append("From");
		sb.append("</br>");
		sb.append(Config.APP_NAME);
		return sb.toString();
	}
}
