package com.example.NewPostAlarmBot.DTO;

import com.example.NewPostAlarmBot.domain.Board;
import lombok.*;


@Getter
@Setter
@Data
@NoArgsConstructor
public class BoardDto {

    public String title;
    public String num;
    public String writer;
    public String url;

    public BoardDto(Board entity){
        this.title = entity.getTitle();
        this.num = entity.getNum();
        this.url = entity.getUrl();
    }

    public BoardDto(String url, String title, String num){
        this.url = url;
        this.title = title;
        this.num = num;
    }

    public Board toEntity(){
        return Board.builder()
                .title(title)
                .num(num)
                .url(url)
                .build();
    }

}
