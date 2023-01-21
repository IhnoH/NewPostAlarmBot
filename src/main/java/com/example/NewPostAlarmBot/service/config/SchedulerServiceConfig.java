package com.example.NewPostAlarmBot.service.config;


import com.example.NewPostAlarmBot.Telegram.TelegramMessageSender;
import com.example.NewPostAlarmBot.repository.BoardRepo;
import com.example.NewPostAlarmBot.repository.DomainInfoRepo;
import com.example.NewPostAlarmBot.Telegram.BoardEditor;
import com.example.NewPostAlarmBot.Telegram.SchedulerService;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@RequiredArgsConstructor
public class SchedulerServiceConfig {
    private final BoardEditor boardEditor;
    private final DomainInfoService domainInfoService;

    @Value("${bot_token}")
    private String botToken;
    private final TelegramMessageSender telegramMessageSender = new TelegramMessageSender(botToken);

    @Bean
    public SchedulerService schedulerService(){
        return new SchedulerService(boardEditor, domainInfoService, telegramMessageSender);
    }

    @Bean
    public ThreadPoolTaskScheduler scheduler(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("timeSchedule");
        taskScheduler.initialize();
        return taskScheduler;
    }

}
