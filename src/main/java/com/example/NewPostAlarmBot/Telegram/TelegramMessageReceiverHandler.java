package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import com.example.NewPostAlarmBot.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@RequiredArgsConstructor
public class TelegramMessageReceiverHandler {

    @Autowired
    private final TelegramMessageSender telegramMessageSender;

    @Autowired
    private final DomainInfoService domainInfoService;

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
//                System.out.println(tmp.size());
//                System.out.println(tmp);
                command = tmp.get(0);
                //System.out.println(tmp[1]);

                if (text.contains(" ")) argument = tmp.get(1);
                String responseText = "";

                if ("/start".equals(command)) {
                    responseText = "/url {새 글 알림을 받고 싶은 주소}\n" +
                            "/stop {알림받는 것을 중지하고 싶은 주소}\n" +
                            "/stopAll 모든 알림을 중지합니다.\n" +
                            "/list 알림받고 있는 주소 리스트를 제공합니다.";
                    //sendMsg(setMsg(chatId, responseText));
                } else if ("/url".equals(command)) {
                    if (argument == null) {
                        //sendMsg(setMsg(chatId, "url 주소를 입력해주세요"));

                        return;
                    }

                    DomainInfoDto d = new DomainInfoDto();
                    d.setUrl(argument);
                    d.setChatId(chatId);

                    domainInfoService.save(d);
                    return;

                } else if ("/list".equals(command)) {
                    List<DomainInfoDto> urlList = domainInfoService.findAll();
                    responseText = "URL List";
                    for(DomainInfoDto d: urlList){
                        if(Objects.equals(chatId, d.chatId))
                            responseText = responseText + "\n" + d.getUrl();
                    }

                } else if ("/stop".equals(command)){
                    try {
                        DomainInfoDto d = domainInfoService.findByUrl(argument);
                        domainInfoService.delete(d);
                        BoardEditor.crawlMap.remove(command);
                        responseText = "정상적으로 중지되었습니다.";
                    }catch (Exception e){
                        responseText = "알림받고 있는 주소가 존재하지 않습니다.";
                    }

                } else if("/login".equals(command)){
                    List<String> arg = Arrays.asList(argument.split(" "));

                    if(arg.size() == 3){
                        DomainInfoDto d = new DomainInfoDto();
                        d.setUrl(arg.get(0));
                        d.setChatId(chatId);
                        d.setLoginId(arg.get(1));
                        d.setLoginPw(arg.get(2));
                        domainInfoService.save(d);
                        return;
                    }
                    else responseText = "입력 형식이 잘못되었습니다.";
                } else if("/stopAll".equals(command)){
                    List<DomainInfoDto> urlList = domainInfoService.findByChatId(chatId);
                    if(urlList.size() == 0) responseText = "알림받고 있는 주소가 존재하지 않습니다.";
                    for(DomainInfoDto d: urlList){
                        domainInfoService.delete(d);
                        BoardEditor.crawlMap.remove(d.url);
                    }
                    responseText = "모든 알림이 정상적으로 중지되었습니다.";
                }
                telegramMessageSender.sendMsg(chatId, responseText);
            }
        }
    }
}

