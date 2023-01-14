package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.Telegram.TelegramMessageSender;
import com.example.NewPostAlarmBot.domain.DomainId;
import com.example.NewPostAlarmBot.repository.JpaBoardRepo;
import com.example.NewPostAlarmBot.repository.JpaDomainRepo;
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
    private final JpaDomainRepo jpaDomainRepo;
    private final JpaBoardRepo jpaBoardRepo;
    private final TelegramMessageSender telegramMessageSender;

    @Autowired
    public static ThreadPoolTaskScheduler scheduler;

    public final Map<String, ScheduledFuture<?>> jobMap = new ConcurrentHashMap<>();


    public SchedulerService(BoardEditor boardEditor, JpaDomainRepo jpaDomainRepo, JpaBoardRepo jpaBoardRepo, TelegramMessageSender telegramMessageSender) {
        this.boardEditor = boardEditor;
        this.jpaDomainRepo = jpaDomainRepo;
        this.jpaBoardRepo = jpaBoardRepo;
        this.telegramMessageSender = telegramMessageSender;
    }

/*
    public List<String> exe(String url){
        System.out.println("run exe");
        List<String> title = new ArrayList<>();
        Runnable run = () -> {job(url, title);};
        jobMap.put(url, scheduler.schedule(run, new PeriodicTrigger(8000, TimeUnit.MILLISECONDS)));

        return title;
    }
*/


    @Async
    @Scheduled(fixedDelay = 5000)
    public void job(){
        List<DomainId> urlList = jpaDomainRepo.findAll();
        //System.out.println("jpaDomainRepo size: "+jpaDomainRepo.findAll().size());

        if(urlList == null || urlList.size() == 0) return;

        List<String> newTitleList;
        for(DomainId tmp: urlList){
            boolean login = true;
            String response = "";
            boardEditor.driverGet(tmp.getUrl());
            boardEditor.init(tmp.getUrl());

            if(tmp.getLoginId() != null && tmp.getLoginPw() != null) {
                login = boardEditor.login(tmp.getUrl(), tmp.getLoginId(), tmp.getLoginPw());
                if(!login){
                    BoardEditor.crawlMap.remove(tmp.getUrl());
                    jpaDomainRepo.delete(tmp);
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



    @Async
    public void register(String key, Long chatId){
        Runnable run = () -> {
            //List<String> newTitle = job(key, chatId);
            //if(newTitle.size() > 0) sendMsg(setMsg(chatId, String.join("\n", newTitle)));
        };
        jobMap.put(key, scheduler.schedule(run, new PeriodicTrigger(4000, TimeUnit.MILLISECONDS)));
    }


    @Async
    public void stop(String url){
        try {
            jobMap.get(url).cancel(true);
            jobMap.remove(url);
            System.out.println("해당 url의 모니터링을 중지합니다.");
        }catch (NullPointerException e){
            System.out.println("해당 url을 모니터링 하고있지 않습니다.");
        }
    }


}
