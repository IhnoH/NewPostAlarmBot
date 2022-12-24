package com.example.NewPostAlarmBot.controller;

import com.example.NewPostAlarmBot.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class tmpController {

//    private final RestApiService restApiService;
//    public RestApiController(RestApiService restApiService) {
//        this.restApiService = restApiService;
//    }

    @Autowired
    private final SchedulerService schedulerService;

    public tmpController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
//        try {
//            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
//            api.registerBot(new TelegramMessageReceiver(schedulerService));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> jobMap_run = new ConcurrentHashMap<>();

    @Async
    @PostMapping(value = "/runnable")
    public HashMap<String, Object> run(String url){
        Runnable run = () -> {
            //schedulerService.job(url, 12L);
        };
        jobMap_run.put(url, scheduler.schedule(run, new PeriodicTrigger(4000, TimeUnit.MILLISECONDS)));

        return null;
    }


    @Async
    @PostMapping(value = "/retTitle2")
    public List<String> retTitle2(String url) throws InterruptedException {
//        List<String> title = schedulerService.job(url, 12L);
//        for(String t: title){
//            System.out.println(t);
//        }
//        return title;
        return null;
    }


    @GetMapping("/shutdown_call")
    public void shutdown_call(String url){
        try {
            jobMap_run.get(url).cancel(true);
            jobMap_run.remove(url);
            System.out.println("해당 url의 모니터링을 중지합니다.");
        }catch (NullPointerException e){
            System.out.println("해당 url을 모니터링 하고있지 않습니다.");
        }
    }


}
