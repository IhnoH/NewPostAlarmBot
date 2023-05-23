package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.Telegram.TelegramMessageSender;
import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
@EnableScheduling
public class SchedulerService{

    private final BoardEditor boardEditor;
    private final DomainInfoService domainInfoService;
    private final TelegramMessageSender telegramMessageSender;

    public SchedulerService(BoardEditor boardEditor, DomainInfoService domainInfoService, TelegramMessageSender telegramMessageSender) {
        this.boardEditor = boardEditor;
        this.domainInfoService = domainInfoService;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Scheduled(fixedDelay = 5000)
    public void job() throws IOException {
        List<DomainInfoDto> urlList = domainInfoService.findAll();
        if(urlList == null || urlList.size() == 0) return;

        List<String> newTitleList;
        for(DomainInfoDto tmp: urlList){
            String url = tmp.getUrl();
            boolean login = true;

            String response = "";
            System.out.println("url: " + url);
            boardEditor.getDoc(url);
            boardEditor.init(tmp);

            if(tmp.getLoginId() != null && tmp.getLoginPw() != null) {
                login = boardEditor.login(url, tmp.getLoginId(), tmp.getLoginPw());
                if(!login){
                    BoardEditor.postMap.remove(url);
                    domainInfoService.delete(tmp);
                    response = "잘못된 로그인 정보입니다. 다시 시도해주세요.";
                    ;
                    telegramMessageSender.sendMsg(SendMessage.builder().chatId(tmp.chatId).text(response).build());
                    continue;
                }
            }

            newTitleList = boardEditor.boardUpdate(url);

            if(newTitleList.isEmpty()) continue;
            //System.out.println(RestApiService.postMap.get(url).getUrlTitle());
            //System.out.println(RestApiService.postMap.get(url).getTopTitle());

            response = BoardEditor.postMap.get(url).getUrlTitle() + "\n\n⦁ " + String.join("\n⦁ ", newTitleList);
            telegramMessageSender.sendMsg(SendMessage.builder().chatId(tmp.chatId).text(response).build());
        }
    }
}
