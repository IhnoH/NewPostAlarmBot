package com.example.NewPostAlarmBot;

import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.service.BoardService;
import com.example.NewPostAlarmBot.service.DomainInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NewPostAlarmBotApplicationTests {

	@Autowired
	DomainInfoService domainInfoService;

	@Test
	void saveTest(){
		DomainInfoDto dto = new DomainInfoDto();
		dto.url = "testUrl";
		dto.chatId = 1234L;
		dto.urlTitle = "testUrlTit";
		domainInfoService.save(dto);

		DomainInfoDto dto2 = domainInfoService.findByUrl("testUrl");
		System.out.println(" ----------------- saveTest: "+Objects.equals(dto2.chatId, dto.chatId));
	}

	@Test
	void updateTest(){
		DomainInfoDto dto2 = domainInfoService.findByUrl("testUrl");
		dto2.urlTitle = "testUrlTit2";
		domainInfoService.save(dto2);
		DomainInfoDto dto3 = domainInfoService.findByUrl("testUrl");
		System.out.println(" ----------------- updateTest: "+Objects.equals(dto2.urlTitle, dto3.urlTitle));
	}

	@Test
	void findByChatIdTest(){
		DomainInfoDto dto = new DomainInfoDto();
		dto.url = "url1";
		dto.chatId = 123L;
		dto.urlTitle = "tit";

		DomainInfoDto dto2 = new DomainInfoDto();
		dto.url = "url1";
		dto.chatId = 123L;
		dto.urlTitle = "tit";

		domainInfoService.save(dto);
		domainInfoService.save(dto2);

		List<DomainInfoDto> dtoList = domainInfoService.findByChatId(123L);
		for(DomainInfoDto d: dtoList){
			System.out.println(" ----------------- findByChatIdTest: "+Objects.equals(d.chatId, 123L));
		}
	}

	@Test
	void test(){

	}


}
