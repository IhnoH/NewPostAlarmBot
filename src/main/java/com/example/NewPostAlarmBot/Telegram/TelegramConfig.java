package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.repository.DomainInfoRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    private final DomainInfoRepo jpaDomainRepo;

    public TelegramConfig(DomainInfoRepo jpaDomainRepo) {
        this.jpaDomainRepo = jpaDomainRepo;
    }

    @Value("${bot_token}")
    private String botToken;

    @Bean
    public TelegramBotsApi TelegramBotRegisterConfig(SchedulerService schedulerService) throws TelegramApiException {


        TelegramMessageSender telegramMessageSender = new TelegramMessageSender(botToken);
        TelegramMessageReceiverHandler telegramMessageReceiverHandler = new TelegramMessageReceiverHandler(telegramMessageSender, schedulerService, jpaDomainRepo);
        TelegramMessageReceiver telegramMessageReceiver = new TelegramMessageReceiver(botToken, telegramMessageReceiverHandler);

        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        telegramBotsApi.registerBot(telegramMessageReceiver);
        return telegramBotsApi;
    }
}
