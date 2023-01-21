package com.example.NewPostAlarmBot.service.config;


import com.example.NewPostAlarmBot.Telegram.TelegramMessageSender;
import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.SchedulerService;
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

    @Bean
    public SchedulerService schedulerService(){
        TelegramMessageSender telegramMessageSender = new TelegramMessageSender(botToken);
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
