package com.example.NewPostAlarmBot.DTO;

import com.example.NewPostAlarmBot.domain.Crawl;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class CrawlDto {
    private String url;
    private String idName;
    private String pwName;
    private int boardSize;
    private String newTitle;
    private String topTitle;
    private String urlTitle;
    private String titleClass;
    private String numClass;

    public CrawlDto(Crawl entity){
        this.url = entity.getUrl();
        this.idName = entity.getIdName();
        this.pwName = entity.getPwName();
        this.boardSize = entity.getBoardSize();
        this.newTitle = entity.getNewTitle();
        this.topTitle = entity.getTopTitle();
        this.titleClass = entity.getTitleClass();
        this.urlTitle = entity.getUrlTitle();
        this.numClass = entity.getNumClass();
    }

    public Crawl toEntity(){
        return Crawl.builder()
                .url(url)
                .idName(idName)
                .pwName(pwName)
                .topTitle(topTitle)
                .newTitle(newTitle)
                .urlTitle(urlTitle)
                .numClass(numClass)
                .titleClass(titleClass)
                .boardSize(boardSize)
                .build();
    }
}
