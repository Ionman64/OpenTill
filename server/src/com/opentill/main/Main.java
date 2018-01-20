package com.opentill.main;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.opentill.httpServer.ServerHandler;
import com.opentill.mail.MailHandler;

public class Main {
		public static void main(String[] args) throws Exception
	    {	
			if (!Config.setup()) {
				System.out.println("System cannot setup configuration properly");
				return;
			}
			Thread thread2 = new Thread() {
				public void run() {
					MailHandler.run();
				}
			};
			thread2.setDaemon(true);
			thread2.start();
			Thread thread1 = new Thread() {
				public void run() {
					ServerHandler.run();
				}
			};
			thread1.setDaemon(true);
			thread1.start();
			Email email = new SimpleEmail();
			email.setSubject("TestMail");
			email.setMsg("This is a test mail ... :-)");
			email.addTo("peterpickerill2016@gmail.com");
			MailHandler.emails.add(email);
	    }
}
