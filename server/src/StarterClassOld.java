//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.varia.NullAppender;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//
//import com.opentill.database.DatabaseHandler;
//import com.opentill.httpServer.ServerHandler;
//import com.opentill.httpServer.ServerHandler2;
//import com.opentill.idata.Order;
//import com.opentill.logging.Log;
//import com.opentill.mail.MailHandler;
//import com.opentill.main.Config;
//import com.opentill.main.Updater;
//import com.opentill.message.MessageHandler;
//import com.opentill.models.SupplierModel;
//import java.awt.*;
//import java.awt.TrayIcon.MessageType;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.net.MalformedURLException;
//
//public class StarterClass {
//	
//	public static void main(String[] args) throws Exception {
//		printOpenTillHeader();
//		displayTray();
//		BasicConfigurator.configure(new NullAppender());
//		if (!Config.setup()) {
//			Log.critical("System cannot setup configuration properly");
//			return;
//		}
//
//		Thread thread2 = new Thread() {
//			@Override
//			public void run() {
//				MailHandler.run();
//			}
//		};
//		thread2.setDaemon(true);
//		thread2.start();
//		
//		//ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//		//long initialDelay = 0L;
//		//long intervalValue = 1L; 
//		//scheduler.scheduleAtFixedRate(new MessageHandler(), initialDelay, intervalValue, TimeUnit.DAYS);
//		
//		//Order.getOrderForSupplier("f9d62c39-70ce-11e7-b34e-426562cc935f");
//		
//		Connection conn = DriverManager.getConnection(Config.databaseProperties.getProperty("hibernate.connection.url"), Config.databaseProperties.getProperty("hibernate.connection.username"), Config.databaseProperties.getProperty("hibernate.connection.password"));
//		if (conn == null) {
//			throw new SQLException("Cannot connect to database - Please check configuration");
//		}
//		conn.close();
//		
//		Configuration configuration = new Configuration().addAnnotatedClass(SupplierModel.class).setProperties(Config.databaseProperties);
//        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
//        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//        
//        
//        Session session = sessionFactory.openSession();
//		//SupplierModel supplier = session.byId( SupplierModel.class ).load("0fd1ef2f-274a-4b3d-b8c4-fca3225bf9f6");
//		//Clear the Session so the person entity becomes detached
//		//session.clear();
//		session.close();
//		serviceRegistry.close();
//		/*System.exit(0);*/
//
//
//         
//		//Updater.updateFromRemoteDatabaseIfNeeded();
//		ServerHandler2.run();
//	}
//	
//    public static void displayTray() throws AWTException, MalformedURLException {
//    	if (!SystemTray.isSupported()) {
//    		return;
//        }
//        //Obtain only one instance of the SystemTray object
//        SystemTray tray = SystemTray.getSystemTray();
//
//        //If the icon is a file
//        //Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
//        //Alternative (if the icon is on the classpath):
//        Image image = Toolkit.getDefaultToolkit().createImage(StarterClass.class.getResource("logo_small.png"));
//
//        TrayIcon trayIcon = new TrayIcon(image, Config.APP_NAME);
//        //Let the system resize the image if needed
//        trayIcon.setImageAutoSize(true);
//        //Set tooltip text for the tray icon
//        trayIcon.setToolTip(Config.APP_NAME);
//        
//        PopupMenu popupMenu = new PopupMenu();
//        popupMenu.add("[Server Running]");
//        
//        MenuItem stopServer = new MenuItem("Stop Server");
//        stopServer.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//        	
//        });
//        popupMenu.add(stopServer);
//        
//        MenuItem startServer = new MenuItem("Start Server");
//        startServer.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				//System.exit(0);
//			}
//        	
//        });
//        startServer.setEnabled(false);
//        popupMenu.add(startServer);
//        
//        trayIcon.setPopupMenu(popupMenu);
//        tray.add(trayIcon);
//
//        //trayIcon.displayMessage(Config.APP_NAME, Config.APP_NAME.concat(" has started"), MessageType.INFO);
//    }
//}