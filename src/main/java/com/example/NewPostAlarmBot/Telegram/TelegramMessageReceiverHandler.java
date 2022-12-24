package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.domain.DomainId;
import com.example.NewPostAlarmBot.repository.JpaDomainRepo;
import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@RequiredArgsConstructor
public class TelegramMessageReceiverHandler {

    @Autowired
    private final TelegramMessageSender telegramMessageSender;
    @Autowired
    private final SchedulerService schedulerService;
    @Autowired
    private final JpaDomainRepo jpaDomainRepo;


    private Map<String, ScheduledFuture<?>> jobMap = new ConcurrentHashMap<>();

    @Async
    public void handle(Update update) {

        Message message = update.getMessage();
        Long chatId = message.getChatId();
//        System.out.println(message.getChatId());
//        System.out.println(message.getText());

        if (update.hasMessage() && update.getMessage().hasText()) {

            String text = message.getText();
            String command;
            String argument = "";


            if (text.indexOf("/") == 0) {

                List<String> tmp = Arrays.asList(text.split(" ", 2));
                System.out.println(tmp.size());
                System.out.println(tmp);
                command = tmp.get(0);
                //System.out.println(tmp[1]);

                if (text.contains(" ")) argument = tmp.get(1);
                String responseText = "";

                if ("/start".equals(command)) {
                    responseText = "/url {새 글 알림을 받고 싶은 url 주소}\n" +
                            "/stop {알림 받는 것을 중지하고 싶은 url 주소}\n" +
                            "/list 알림 받고 있는 url 주소 리스트 제공";
                    //sendMsg(setMsg(chatId, responseText));
                } else if ("/url".equals(command)) {
                    if (argument == null) {
                        //sendMsg(setMsg(chatId, "url 주소를 입력해주세요"));

                        return;
                    }

                    DomainId d = new DomainId();
                    d.setUrl(argument);
                    d.setChatId(chatId);
                    jpaDomainRepo.save(d);
                    return;

                } else if ("/list".equals(command)) {
                    List<DomainId> urlList = jpaDomainRepo.findAll();
                    responseText = "URL List";
                    for(DomainId d: urlList){
                        responseText = responseText + "\n" + d.getUrl();
                    }

                } else if ("/stop".equals(command)){
                    Optional<DomainId> d =  jpaDomainRepo.findByUrl(argument);
                    if (d.isEmpty()) responseText = "잘못된 url입니다.";
                    else{
                        jpaDomainRepo.delete(d.get());
                        BoardEditor.crawlMap.remove(command);
                        responseText = "정상적으로 중지되었습니다.";
                    }
                } else if("/login".equals(command)){
                    List<String> arg = Arrays.asList(argument.split(" "));

                    if(arg.size() == 3){
                        DomainId d = new DomainId();
                        d.setUrl(arg.get(0));
                        d.setChatId(chatId);
                        d.setLoginId(arg.get(1));
                        d.setLoginPw(arg.get(2));
                        jpaDomainRepo.save(d);
                        return;
                    }
                    else responseText = "입력 형식이 잘못되었습니다.";

                }
                telegramMessageSender.sendMessage(chatId, responseText);
            }
        }
    }
}

