package com.example.NewPostAlarmBot.service;


import com.example.NewPostAlarmBot.DTO.CrawlDto;
import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.domain.DomainInfo;
import com.example.NewPostAlarmBot.repository.DomainInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class DomainInfoService {
    private final DomainInfoRepo domainInfoRepo;

    public void save(DomainInfoDto dto){
        domainInfoRepo.save(dto.toEntity());
    }

    public String update(String url, DomainInfoDto dto){
        DomainInfo domainInfo = domainInfoRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("Not found URL"));
        domainInfo = dto.toEntity();
        return url;
    }

    public List<DomainInfoDto> findAll(){
        List<DomainInfoDto> tmp = new ArrayList<>();

        for(DomainInfo d: domainInfoRepo.findAll())
            tmp.add(new DomainInfoDto(d));
        return tmp;
    }

    public DomainInfoDto findByUrl(String url) {
        DomainInfo domainInfo = domainInfoRepo.findByUrl(url).orElseThrow(() -> new IllegalArgumentException("Not found URL"));
        return new DomainInfoDto(domainInfo);
    }

    public void delete(DomainInfoDto dto){
        DomainInfo domainInfo = domainInfoRepo.findByUrl(dto.getUrl()).orElseThrow(() -> new IllegalArgumentException("Not found URL"));
        domainInfoRepo.delete(domainInfo);
    }
}
