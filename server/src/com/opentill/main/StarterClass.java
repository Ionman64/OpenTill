package com.opentill.main;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.opentill.mail.MailHandler;

@SpringBootApplication
public class StarterClass {

	public static void main(String[] args) {
		//System.out.println(Utils.generateAsciiLogo());
		SpringApplication.run(StarterClass.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			Thread thread2 = new Thread() {
				@Override
				public void run() {
					MailHandler.run();
				}
			};
			thread2.setDaemon(true);
			thread2.start();
		};
	}

}
