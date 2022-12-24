package com.example.NewPostAlarmBot.service.config;


import com.example.NewPostAlarmBot.repository.JpaBoardRepo;
import com.example.NewPostAlarmBot.repository.JpaCrawlRepo;
import com.example.NewPostAlarmBot.service.BoardEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiServiceConfig {

    private final JpaCrawlRepo jpaCrawlRepo;
    private final JpaBoardRepo jpaBoardRepo;

    //@Autowired
    public RestApiServiceConfig(JpaCrawlRepo jpaCrawlRepo, JpaBoardRepo jpaBoardRepo) {
        this.jpaCrawlRepo = jpaCrawlRepo;
        this.jpaBoardRepo = jpaBoardRepo;
    }

    @Bean
    public BoardEditor restApiService(){
        return new BoardEditor(jpaCrawlRepo, jpaBoardRepo);
    }
}
