package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.BoardDto;
import com.example.NewPostAlarmBot.DTO.CrawlDto;
import com.example.NewPostAlarmBot.domain.Board;
import com.example.NewPostAlarmBot.domain.Crawl;
import com.example.NewPostAlarmBot.repository.CrawlRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CrawlService {
    private final CrawlRepo crawlRepo;

    public void save(CrawlDto dto){
        crawlRepo.save(dto.toEntity());
    }

    public String update(String url, CrawlDto dto){
        Crawl crawl = crawlRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("Not found URL"));
        crawl = dto.toEntity();
        return url;
    }

    public CrawlDto findByUrl(String url){
        Crawl crawl = crawlRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("Not found URL"));
        return new CrawlDto(crawl);
    }


}
