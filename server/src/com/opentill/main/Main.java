package com.opentill.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.opentill.document.ExcelHelper;
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
			email.setSubject("OpenTill Server Started");
			email.setMsg("Your OpenTill server instance has started at " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
			email.addTo("peterpickerill2014@gmail.com");
			//MailHandler.emails.add(email);
			new ExcelHelper().testWrite();
	    }
}
