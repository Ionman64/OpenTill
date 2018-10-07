
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import com.opentill.httpServer.ServerHandler;
import com.opentill.idata.Order;
import com.opentill.logging.Log;
import com.opentill.mail.MailHandler;
import com.opentill.main.Config;
import com.opentill.main.Updater;
import com.opentill.message.MessageHandler;

public class StarterClass {
	public static void printOpenTillHeader() {
		System.out.println(
				" _____                    _____ _ _ _ \r\n" + 
				"|  _  |                  |_   _(_) | |\r\n" + 
				"| | | |_ __   ___ _ __     | |  _| | |\r\n" + 
				"| | | | '_ \\ / _ \\ '_ \\    | | | | | |\r\n" + 
				"\\ \\_/ / |_) |  __/ | | |   | | | | | |\r\n" + 
				" \\___/| .__/ \\___|_| |_|   \\_/ |_|_|_| v" + Config.CURRENT_LOCAL_VERSION +"\r\n" + 
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
		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		long initialDelay = 0L;
		long intervalValue = 1L; 
		scheduler.scheduleAtFixedRate(new MessageHandler(), initialDelay, intervalValue, TimeUnit.DAYS);
		
		Order.getOrderForSupplier("f9d62c39-70ce-11e7-b34e-426562cc935f");
		//return;
		// new SparkTest();
		
		Updater.updateFromRemoteDatabaseIfNeeded();
		ServerHandler.run();
	}
}