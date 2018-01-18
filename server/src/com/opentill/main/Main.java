package com.opentill.main;

import com.opentill.httpServer.ServerHandler;

public class Main {
		public static void main(String[] args) throws Exception
	    {	
			if (!Config.setup()) {
				System.out.println("System cannot setup configuration properly");
				return;
			}
			ServerHandler.run();
	    }
}
