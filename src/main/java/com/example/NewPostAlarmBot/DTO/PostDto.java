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
public class PostDto {
    public String url;
    public String idName;
    public String pwName;
    public int boardSize;
    public String newTitle;
    public String topTitle;
    public String urlTitle;
    public String titleClass;
    public String numClass;
    public String keyword;

    public PostDto(Crawl entity){
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
