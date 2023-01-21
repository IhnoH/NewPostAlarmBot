package com.example.NewPostAlarmBot.Telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.*;



public class TelegramMessageReceiver extends TelegramLongPollingBot {



    private final String botToken;
    @Autowired
    private final TelegramMessageReceiverHandler telegramMessageReceiverHandler;
    @Autowired
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public TelegramMessageReceiver(String botToken, TelegramMessageReceiverHandler telegramMessageReceiverHandler) {
        this.botToken = botToken;
        this.telegramMessageReceiverHandler = telegramMessageReceiverHandler;
    }


    @Override
    public String getBotUsername() {return "newPostAlarmbot";}
    @Override
    public String getBotToken() {return botToken;}


    @Async
    @Override
    public void onUpdateReceived(Update update) {
        executorService.submit(()->{
            telegramMessageReceiverHandler.handle(update);
        });

    }
}
