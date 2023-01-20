package com.example.NewPostAlarmBot.domain;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Crawl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String url;
    public String idName;
    public String pwName;
    public int boardSize;
    public String newTitle;
    public String topTitle;
    public String urlTitle;
    public String titleClass;
    public String numClass;


    @Builder
    public Crawl(String url, String idName, String pwName, int boardSize, String newTitle, String topTitle, String urlTitle, String titleClass, String numClass){
        this.url = url;
        this.idName = idName;
        this.pwName = pwName;
        this.boardSize = boardSize;
        this.newTitle = newTitle;
        this.topTitle = topTitle;
        this.titleClass = titleClass;
        this.urlTitle = urlTitle;
        this.numClass = numClass;
    }


}
