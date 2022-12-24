package com.example.NewPostAlarmBot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewPostAlarmBotApplication {
	public static void main(String[] args) {
		WebDriverManager.chromedriver().setup();
		SpringApplication.run(NewPostAlarmBotApplication.class, args);

	}
}
