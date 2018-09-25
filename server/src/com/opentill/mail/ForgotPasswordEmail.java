package com.opentill.mail;

import java.util.Collection;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

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
		return String.format("%s Password Reset", Config.APP_NAME);
	}
	public Email toEmail() throws EmailException {
		Email msg = new HtmlEmail();
		msg.addTo(this.respondentEmail);
        msg.setSubject(this.getSubject());
		msg.setMsg(this.getBody());
		return msg;
	}
	public String getBody() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Hello %s", this.name));
		sb.append("</br>");
		sb.append(String.format("You have requested a password reset, please <a href='%s'>Click Here</a>", this.passwordLink));
		sb.append("If you cannot click on the above link, please copy and paste the below link");
		sb.append("</br>");
		sb.append(this.passwordLink);
		sb.append("</br>");
		sb.append("From");
		sb.append(Config.APP_NAME);
		return sb.toString();
	}
}
