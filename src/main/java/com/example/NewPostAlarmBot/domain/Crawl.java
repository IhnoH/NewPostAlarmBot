package com.example.NewPostAlarmBot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String url;

    private String idName;
    private String pwName;

    private int boardSize;

    private String newTitle;

    private String topTitle;

    private String urlTitle;

    private String titleClass = "";

    private String numClass = "";

}
