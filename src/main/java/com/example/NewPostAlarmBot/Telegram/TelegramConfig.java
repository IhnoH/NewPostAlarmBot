package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.repository.JpaDomainRepo;
import com.example.NewPostAlarmBot.service.SchedulerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    private final JpaDomainRepo jpaDomainRepo;

    public TelegramConfig(JpaDomainRepo jpaDomainRepo) {
        this.jpaDomainRepo = jpaDomainRepo;
    }

    @Bean
    public TelegramBotsApi TelegramBotRegisterConfig(SchedulerService schedulerService) throws TelegramApiException {

        String botToken = "5765206121:AAH21VfSPgLSVtNZCvAQLr2ssqvbdP8GZiE";

        TelegramMessageSender telegramMessageSender = new TelegramMessageSender(botToken);
        TelegramMessageReceiverHandler telegramMessageReceiverHandler = new TelegramMessageReceiverHandler(telegramMessageSender, schedulerService, jpaDomainRepo);
        TelegramMessageReceiver telegramMessageReceiver = new TelegramMessageReceiver(botToken, telegramMessageReceiverHandler);

        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        telegramBotsApi.registerBot(telegramMessageReceiver);
        return telegramBotsApi;
    }
}
