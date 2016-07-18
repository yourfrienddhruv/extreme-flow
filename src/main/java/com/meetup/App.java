package com.meetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.TimeZone;

/**
 * The Application
 */
@SpringBootApplication
public class App {

	@Autowired
	private Environment env;

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	/*private static void setTimeZone(final Environment env) {
		*//*Set default timezone to UTC if app.timezone is not found*//*
		final String appTimeZone = env.getProperty("app.timezone", "Etc/UTC");
		TimeZone.setDefault(TimeZone.getTimeZone(appTimeZone));
	}*/
}
