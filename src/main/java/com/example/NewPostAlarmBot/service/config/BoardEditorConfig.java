package com.example.NewPostAlarmBot.service.config;

import com.example.NewPostAlarmBot.service.BoardEditor;
import com.example.NewPostAlarmBot.service.BoardService;
import com.example.NewPostAlarmBot.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BoardEditorConfig {
    private final PostService postService;
    private final BoardService boardService;

    @Bean
    public BoardEditor boardEditor(){
        return new BoardEditor(postService, boardService);
    }
}
