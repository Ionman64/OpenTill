
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

import com.opentill.document.PDFHelper;
import com.opentill.httpServer.ServerHandler;
import com.opentill.logging.Log;
import com.opentill.mail.MailHandler;
import com.opentill.main.Config;

public class StarterClass {
	public static void printOpenTillHeader() {
		System.out.println(
				" _____                    _____ _ _ _ \r\n" + 
				"|  _  |                  |_   _(_) | |\r\n" + 
				"| | | |_ __   ___ _ __     | |  _| | |\r\n" + 
				"| | | | '_ \\ / _ \\ '_ \\    | | | | | |\r\n" + 
				"\\ \\_/ / |_) |  __/ | | |   | | | | | |\r\n" + 
				" \\___/| .__/ \\___|_| |_|   \\_/ |_|_|_| v" + Config.CURRENT_VERSION +"\r\n" + 
				"      | |                             \r\n" + 
				"      |_|                             \r\n");
	}
	public static void main(String[] args) throws Exception {
		printOpenTillHeader();
		BasicConfigurator.configure(new NullAppender());
		if (!Config.setup()) {
			Log.critical("System cannot setup configuration properly");
			return;
		}

		Thread thread2 = new Thread() {
			@Override
			public void run() {
				MailHandler.run();
			}
		};
		thread2.setDaemon(true);
		thread2.start();
		//PDFHelper.createPDF();
		//return;

		// new SparkTest();

		// Updater.update();

		// DatabaseMigration db = new DatabaseMigration(Config.CURRENT_VERSION);
		// db.up();

		ServerHandler.run();
	}
	
}