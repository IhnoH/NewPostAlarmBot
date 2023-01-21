package com.example.NewPostAlarmBot.service.config;

import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.BoardService;
import com.example.NewPostAlarmBot.service.CrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BoardEditorConfig {
    private final CrawlService crawlService;
    private final BoardService boardService;

    @Bean
    public BoardEditor boardEditor(){
        return new BoardEditor(crawlService, boardService);
    }
}
