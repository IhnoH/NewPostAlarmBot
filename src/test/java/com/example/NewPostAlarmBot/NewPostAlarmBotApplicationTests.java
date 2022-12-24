package com.example.NewPostAlarmBot;

import com.example.NewPostAlarmBot.domain.Crawl;
import com.example.NewPostAlarmBot.repository.JpaCrawlRepo;
import com.example.NewPostAlarmBot.service.BoardEditor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NewPostAlarmBotApplicationTests {

	@Autowired
	JpaCrawlRepo jpaCrawlRepo;

	@Autowired
    BoardEditor boardEditor;

	@Test
	void saveTest(){
		Crawl crawl = new Crawl();
		crawl.setUrl("testUrl");


	}

	@Test
	void findByUrlTest(){
		Crawl crawl1 = new Crawl();
		Crawl crawl2 = new Crawl();

		crawl1.setUrl("crawl1");
		crawl1.setNewTitle("newTitle1");
		jpaCrawlRepo.save(crawl1);

		crawl2.setUrl("crawl2");
		crawl2.setNewTitle("newTitle2");
		jpaCrawlRepo.save(crawl2);


		try{

			System.out.println(jpaCrawlRepo.findByUrl(crawl1.getUrl()).get().getClass());

			//assertEquals(test.getUrl(), crawl1.getUrl());

		}catch (IllegalStateException e){
			System.out.println(e.getMessage());
		}
	}

}
