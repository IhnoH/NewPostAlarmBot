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

import javax.transaction.Transactional;
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
    public void job(){
        List<DomainInfoDto> urlList = domainInfoService.findAll();
        if(urlList == null || urlList.size() == 0) return;

        List<String> newTitleList;
        for(DomainInfoDto tmp: urlList){
            boolean login = true;
            String response = "";
            boardEditor.driverGet(tmp.getUrl());
            boardEditor.init(tmp.getUrl());

            if(tmp.getLoginId() != null && tmp.getLoginPw() != null) {
                login = boardEditor.login(tmp.getUrl(), tmp.getLoginId(), tmp.getLoginPw());
                if(!login){
                    BoardEditor.crawlMap.remove(tmp.getUrl());
                    domainInfoService.delete(tmp);
                    response = "잘못된 로그인 정보입니다. 다시 시도해주세요.";
                    telegramMessageSender.sendMsg(tmp.getChatId(), response);
                    continue;
                }
            }

            newTitleList = boardEditor.boardUpdate(tmp.getUrl());

            if(newTitleList.isEmpty()) continue;
            //System.out.println(RestApiService.crawlMap.get(tmp.getUrl()).getUrlTitle());
            //System.out.println(RestApiService.crawlMap.get(tmp.getUrl()).getTopTitle());

            response = BoardEditor.crawlMap.get(tmp.getUrl()).getUrlTitle() + "\n\n- " + String.join("\n- ", newTitleList);
            telegramMessageSender.sendMsg(tmp.getChatId(), response);
        }
    }
}
