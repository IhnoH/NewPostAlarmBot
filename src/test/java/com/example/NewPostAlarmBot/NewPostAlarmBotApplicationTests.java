package com.example.NewPostAlarmBot;

import com.example.NewPostAlarmBot.domain.Board;
import com.example.NewPostAlarmBot.domain.Crawl;
import com.example.NewPostAlarmBot.repository.JpaBoardRepo;
import com.example.NewPostAlarmBot.repository.JpaCrawlRepo;
import com.example.NewPostAlarmBot.service.BoardEditor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NewPostAlarmBotApplicationTests {

	@Autowired
	JpaCrawlRepo jpaCrawlRepo;

	@Autowired
	JpaBoardRepo jpaBoardRepo;

	@Autowired
    BoardEditor boardEditor;


	@Test
	void boardRepoFindByUrlTest(){
		Board tmp = new Board();
		tmp.setUrl("testUrl1");

		jpaBoardRepo.save(tmp);
		Board tmp2 = jpaBoardRepo.findByUrl("testUrl1");

		System.out.println(Objects.equals(tmp2.getUrl(), tmp.getUrl()));
	}

}
