package com.example.NewPostAlarmBot.Telegram.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private String token;
    private Long chatId;
    private String text;

    public Message(String token, Long chatId, String text) {
        this.token = token;
        this.chatId = chatId;
        this.text = text;
    }

    public static Message create(String token, Long chatId, String text){
        return new Message(token, chatId, text);
    }
}
