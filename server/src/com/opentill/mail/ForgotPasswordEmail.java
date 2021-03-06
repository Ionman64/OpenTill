package com.opentill.mail;


import org.apache.commons.mail.EmailException;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;

import com.opentill.main.Config;

public class ForgotPasswordEmail {
	public String name;
	public String id;
	public String respondentEmail;
	public String passwordLink;
	public ForgotPasswordEmail(String id, String name, String respondentEmail, String passwordLink) {
		this.id = id;
		this.name = name;
		this.respondentEmail = respondentEmail;
		this.passwordLink = passwordLink;
	}
	public String getSubject() {
		return String.format("[%s] Password Reset", Config.APP_NAME);
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
		sb.append(String.format("You have requested a password reset, please <a href='%s'>Click Here</a>", this.passwordLink));
		sb.append("</br>");
		sb.append("If you cannot click on the above link, please copy and paste the below link");
		sb.append("</br>");
		sb.append("</br>");
		sb.append(this.passwordLink);
		sb.append("</br>");
		sb.append("</br>");
		sb.append("From");
		sb.append("</br>");
		sb.append(Config.APP_NAME);
		return sb.toString();
	}
}
