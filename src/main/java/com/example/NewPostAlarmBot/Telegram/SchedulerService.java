package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.domain.DomainInfo;
import com.example.NewPostAlarmBot.repository.DomainInfoRepo;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

//@Service
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
                    telegramMessageSender.sendMessage(tmp.getChatId(), response);
                    continue;
                }
            }

            newTitleList = boardEditor.boardUpdate(tmp.getUrl());

            if(newTitleList.isEmpty()) continue;
            //System.out.println(RestApiService.crawlMap.get(tmp.getUrl()).getUrlTitle());
            //System.out.println(RestApiService.crawlMap.get(tmp.getUrl()).getTopTitle());

            response = BoardEditor.crawlMap.get(tmp.getUrl()).getUrlTitle() + "\n\n- " + String.join("\n- ", newTitleList);
            telegramMessageSender.sendMessage(tmp.getChatId(), response);

        }

    }

}
