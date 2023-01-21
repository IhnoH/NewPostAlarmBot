package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.repository.DomainInfoRepo;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import com.example.NewPostAlarmBot.service.SchedulerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    private final DomainInfoService domainInfoService;

    public TelegramConfig(DomainInfoService domainInfoService) {
        this.domainInfoService = domainInfoService;
    }

    @Value("${botToken}")
    private String botToken;

    @Bean
    public TelegramBotsApi TelegramBotRegisterConfig() throws TelegramApiException {
        TelegramMessageSender telegramMessageSender = new TelegramMessageSender(botToken);
        TelegramMessageReceiverHandler telegramMessageReceiverHandler = new TelegramMessageReceiverHandler(telegramMessageSender, domainInfoService);
        TelegramMessageReceiver telegramMessageReceiver = new TelegramMessageReceiver(botToken, telegramMessageReceiverHandler);

        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        telegramBotsApi.registerBot(telegramMessageReceiver);
        return telegramBotsApi;

    }
}
