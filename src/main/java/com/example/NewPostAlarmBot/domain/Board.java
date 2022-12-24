package com.example.NewPostAlarmBot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Board(String url, String title, String num){
        this.title = title;
        this.num = num;
        this.url = url;
    }

    public Board(){
        this.title = "";
        this.num = "";
        this.writer = "";
    }

    public String title = "";
    public String num = "";
    public String writer = "";
    public String url = "";
}