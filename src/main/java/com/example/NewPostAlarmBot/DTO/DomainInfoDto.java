package com.example.NewPostAlarmBot.DTO;

import com.example.NewPostAlarmBot.domain.DomainInfo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class DomainInfoDto {
    private Long chatId;
    private String url;
    private String urlTitle;
    private String loginId;
    private String loginPw;

    public DomainInfoDto(DomainInfo entity){
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
