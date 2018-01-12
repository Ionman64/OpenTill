package com.opentill.mail;

import java.util.ArrayList;

public class EmailMessage {
	private ArrayList<String> recipients = new ArrayList<String>();
	private String subject = null;
	private boolean hasBeenSent = false;
	private String body = null;
	public String[] getRecipients() {
		return (String[]) recipients.toArray();
	}
	public void addRecipient(String recipient) {
		this.recipients.add(recipient);
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public boolean isHasBeenSent() {
		return hasBeenSent;
	}
	public void setHasBeenSent(boolean hasBeenSent) {
		this.hasBeenSent = hasBeenSent;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
