package com.opentill.main;

import org.json.simple.JSONObject;

import com.opentill.database.DatabaseMigration;
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
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("text", "Server Started");

			
			DatabaseMigration db = new DatabaseMigration(Config.CURRENT_VERSION);
			db.up();
			
			ServerHandler.run();
	    }
}
