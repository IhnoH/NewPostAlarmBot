package com.example.NewPostAlarmBot.Telegram;

import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import com.example.NewPostAlarmBot.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@RequiredArgsConstructor
public class TelegramMessageReceiverHandler {

    @Autowired
    private final TelegramMessageSender telegramMessageSender;

    @Autowired
    private final DomainInfoService domainInfoService;

    private boolean requestUrl = false;

    @Async
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        SendMessage sendMessage = new SendMessage();
        String command;
        String argument = "";
        String responseText = "";

        if ((update.hasMessage() && update.getMessage().hasText())) {
            String text = message.getText();

            if(requestUrl && text.contains("https://")) {
                DomainInfoDto d = new DomainInfoDto();
                d.setUrl(text);
                d.setChatId(chatId);
                domainInfoService.save(d);
                log.info("added url {} by {}", text, chatId);
                responseText = "개시판에 새 글이 업데이트되면 제목을 알려드립니다.";
                requestUrl = false;
            }else{
                responseText = "주소 형식을 지켜주세요.";
                requestUrl = false;
            }

            if (text.indexOf("/") == 0) {
                List<String> tmp = Arrays.asList(text.split(" ", 2));
                command = tmp.get(0);

                if (text.contains(" ")) argument = tmp.get(1);

                if ("/start".equals(command)) {

                    sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(List.of(
                            List.of(InlineKeyboardButton.builder().text("알림 받는 주소 추가").callbackData("addUrl "+chatId).build()),
                            List.of(InlineKeyboardButton.builder().text("알림받는 주소 목록").callbackData("list "+chatId).build()),
                            List.of(InlineKeyboardButton.builder().text("원하는 주소 알림 중지").callbackData("stop "+chatId).build()),
                            List.of(InlineKeyboardButton.builder().text("모든 주소 알림 중지").callbackData("stopAll "+chatId).build())
                    )).build());

                    responseText = "Start New Post Alarm Bot";
                    log.info("called start by {}", chatId);

                } else if ("/url".equals(command)) {
                    if (argument == null || argument.length() == 0) {
                        telegramMessageSender.sendMsg(SendMessage.builder().chatId(chatId).text("url 주소를 입력해주세요").build());
                        return;
                    }
                    DomainInfoDto d = new DomainInfoDto();
                    d.setUrl(argument);
                    d.setChatId(chatId);
                    domainInfoService.save(d);
                    log.info("added url {} by {}", argument, chatId);
                    responseText = "개시판에 새 글이 업데이트되면 제목을 알려드립니다.";
                }else if ("/stop".equals(command)){
                    try {
                        DomainInfoDto d = domainInfoService.findByUrl(argument);
                        domainInfoService.delete(d);
                        BoardEditor.crawlMap.remove(command);
                        responseText = "정상적으로 중지되었습니다.";
                    }catch (Exception e){
                        responseText = "알림받고 있는 주소가 존재하지 않습니다.";
                    }
                    log.info("stop alarm {} by {}", argument, chatId);
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
                }
            }

            sendMessage.setChatId(chatId);
            sendMessage.setText(responseText);
            telegramMessageSender.sendMsg(sendMessage);
        }

    }


    @Async
    public void callbackHandle(Update update){
        List<String> callback = List.of(update.getCallbackQuery().getData().split(" "));

        SendMessage sendMessage = new SendMessage();
        Long chatId = Long.parseLong(callback.get(1));
        String command = callback.get(0);
        String argument = "";
        if(callback.size() == 3) argument = callback.get(2);
        String responseText = "";

        if("addUrl".equals(command)){
            requestUrl = !requestUrl;
            responseText = "알림받고 싶은 게시판 주소를 복사하여 공백없이 입력해주세요.";
            log.info("request add by {}", chatId);

        }else if("list".equals(command)){
            log.info("list called by {}", chatId);
            List<List<InlineKeyboardButton>> tmp = new ArrayList<>();
            List<DomainInfoDto> dtoList = domainInfoService.findByChatId(chatId);
            if(dtoList.size() == 0){
                emptyList(chatId);
                return;
            }
            for(DomainInfoDto dto: dtoList){
                List<InlineKeyboardButton> tmp2 = new ArrayList<>();
                String s = String.join(" ", new String[]{"urlMenu", chatId.toString(), dto.id.toString()});
                System.out.println(s);
                tmp2.add(InlineKeyboardButton.builder().callbackData(s).text(dto.url).build());
                tmp.add(tmp2);
            }
            responseText = "알림 받고 있는 주소 목록";
            sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(tmp).build());
        }else if("stop".equals(command)){
            log.info("stop list by {}", chatId);
            List<List<InlineKeyboardButton>> tmp = new ArrayList<>();
            List<DomainInfoDto> dtoList = domainInfoService.findByChatId(chatId);
            if(dtoList.size() == 0){
                emptyList(chatId);
                return;
            }
            for(DomainInfoDto dto: dtoList){
                List<InlineKeyboardButton> tmp2 = new ArrayList<>();
                String s = String.join(" ", new String[]{"delete", chatId.toString(), dto.id.toString()});
                tmp2.add(InlineKeyboardButton.builder().text(dto.url).callbackData(s).build());
                tmp.add(tmp2);
            }
            sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder().keyboard(tmp).build());
            responseText = "알림을 중지하고 싶은 주소를 눌러주세요.";
        }else if("delete".equals(command)){
            for(DomainInfoDto dto: domainInfoService.findByChatId(chatId)){
                if(dto.id.toString().equals(argument)){
                    domainInfoService.delete(dto);
                    BoardEditor.crawlMap.remove(dto.url);
                    log.info("delete url: {} by {}", dto.url, dto.chatId);
                    break;
                }
            }
            responseText = "정상적으로 중지되었습니다.";
        }else if("stopAll".equals(command)){
            log.info("delete all url by {}", chatId);
            List<DomainInfoDto> urlList = domainInfoService.findByChatId(chatId);
            if(urlList.size() == 0) {
                emptyList(chatId);
                return;
            }
            for(DomainInfoDto d: urlList){
                domainInfoService.delete(d);
                BoardEditor.crawlMap.remove(d.url);
            }
            responseText = "모든 알림이 정상적으로 중지되었습니다.";
        }

        sendMessage.setChatId(chatId);
        sendMessage.setText(responseText);
        telegramMessageSender.sendMsg(sendMessage);
    }

    public void emptyList(Long chatId){
        String responseText = "알림받는 주소가 없습니다.";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(responseText);
        telegramMessageSender.sendMsg(sendMessage);
    }
}

