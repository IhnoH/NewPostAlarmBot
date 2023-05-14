package com.example.NewPostAlarmBot.service;

import com.example.NewPostAlarmBot.DTO.PostDto;
import com.example.NewPostAlarmBot.domain.Crawl;
import com.example.NewPostAlarmBot.repository.PostRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepo postRepo;

    public void save(PostDto dto){
        Optional<Crawl> b = postRepo.findByUrl(dto.url);
        if(b.isPresent())
            update(dto.url, dto);
        else
            postRepo.save(dto.toEntity());
    }

    public String update(String url, PostDto dto){
        Crawl crawl = postRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("Not found URL"));
        crawl = dto.toEntity();
        return url;
    }

    public PostDto findByUrl(String url){
        Crawl crawl = postRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("Not found URL"));
        return new PostDto(crawl);
    }


}
