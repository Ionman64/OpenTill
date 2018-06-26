
import org.apache.log4j.BasicConfigurator;

import com.opentill.document.PDFHelper;
import com.opentill.httpServer.ServerHandler;
import com.opentill.mail.MailHandler;
import com.opentill.main.Config;

public class StarterClass {
	public static void main(String[] args) throws Exception {
		//BasicConfigurator.configure();
		if (!Config.setup()) {
			System.out.println("System cannot setup configuration properly");
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

		PDFHelper.createPDF();

		// new SparkTest();

		// Updater.update();

		// DatabaseMigration db = new DatabaseMigration(Config.CURRENT_VERSION);
		// db.up();

		ServerHandler.run();
	}
}