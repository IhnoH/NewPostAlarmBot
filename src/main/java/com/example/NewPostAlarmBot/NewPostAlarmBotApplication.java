package com.example.NewPostAlarmBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewPostAlarmBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(NewPostAlarmBotApplication.class, args);

	}
}
