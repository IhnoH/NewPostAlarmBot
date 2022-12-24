package com.example.NewPostAlarmBot.Telegram;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramMessageSender extends DefaultAbsSender {

    private final String botToken;

    public TelegramMessageSender(String botToken) {
        super(new DefaultBotOptions());
        this.botToken = botToken;
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendMessage(Long chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        sendMessage.enableHtml(true);
        sendMessage.enableMarkdown(false);

        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
