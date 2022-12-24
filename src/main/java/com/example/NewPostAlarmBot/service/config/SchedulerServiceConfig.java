package com.example.NewPostAlarmBot.service.config;


import com.example.NewPostAlarmBot.Telegram.TelegramMessageSender;
import com.example.NewPostAlarmBot.repository.JpaBoardRepo;
import com.example.NewPostAlarmBot.repository.JpaDomainRepo;
import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@RequiredArgsConstructor
public class SchedulerServiceConfig {
    private final BoardEditor boardEditor;
    private final JpaDomainRepo jpaDomainRepo;
    private final JpaBoardRepo jpaBoardRepo;
    private String botToken = "5765206121:AAH21VfSPgLSVtNZCvAQLr2ssqvbdP8GZiE";
    private final TelegramMessageSender telegramMessageSender = new TelegramMessageSender(botToken);

    @Bean
    public SchedulerService schedulerService(){
        return new SchedulerService(boardEditor, jpaDomainRepo, jpaBoardRepo, telegramMessageSender);
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
