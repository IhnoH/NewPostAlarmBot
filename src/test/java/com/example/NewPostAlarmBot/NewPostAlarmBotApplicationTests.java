package com.example.NewPostAlarmBot;

import com.example.NewPostAlarmBot.DTO.BoardDto;
import com.example.NewPostAlarmBot.domain.Board;
import com.example.NewPostAlarmBot.repository.BoardRepo;
import com.example.NewPostAlarmBot.repository.CrawlRepo;
import com.example.NewPostAlarmBot.Telegram.BoardEditor;
import com.example.NewPostAlarmBot.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NewPostAlarmBotApplicationTests {

	@Autowired
	BoardService boardService;

}
