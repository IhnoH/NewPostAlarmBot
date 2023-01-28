package com.example.NewPostAlarmBot.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DomainInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Long chatId;
    private String url;
    private String urlTitle;

    private String loginId;
    private String loginPw;

    @Builder
    public DomainInfo(Long chatId, String url, String urlTitle, String loginId, String loginPw){
        this.chatId = chatId;
        this.url = url;
        this.urlTitle = urlTitle;
        this.loginId = loginId;
        this.loginPw = loginPw;
    }

}
