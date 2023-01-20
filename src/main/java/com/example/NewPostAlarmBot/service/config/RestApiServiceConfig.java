package com.example.NewPostAlarmBot.service.config;


import com.example.NewPostAlarmBot.repository.BoardRepo;
import com.example.NewPostAlarmBot.repository.CrawlRepo;
import com.example.NewPostAlarmBot.Telegram.BoardEditor;
import com.example.NewPostAlarmBot.service.BoardService;
import com.example.NewPostAlarmBot.service.CrawlService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiServiceConfig {
    private final CrawlService crawlService;
    private final BoardService boardService;

    public RestApiServiceConfig(CrawlService crawlService, BoardService boardService) {
        this.crawlService = crawlService;
        this.boardService = boardService;
    }

    @Bean
    public BoardEditor restApiService(){
        return new BoardEditor(crawlService, boardService);
    }
}
