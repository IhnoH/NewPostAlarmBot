package com.example.NewPostAlarmBot.DTO;

import com.example.NewPostAlarmBot.domain.DomainInfo;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
public class DomainInfoDto {
    public Integer id;
    public Long chatId;
    public String url;
    public String urlTitle;
    public String loginId;
    public String loginPw;

    public DomainInfoDto(DomainInfo entity){
        this.id = entity.getId();
        this.url = entity.getUrl();
        this.urlTitle = entity.getUrlTitle();
        this.chatId = entity.getChatId();
        this.loginId = entity.getLoginId();
        this.loginPw = entity.getLoginPw();
    }

    public DomainInfo toEntity(){
        return DomainInfo.builder()
                .url(url)
                .urlTitle(urlTitle)
                .chatId(chatId)
                .loginId(loginId)
                .loginPw(loginPw)
                .build();
    }
}
