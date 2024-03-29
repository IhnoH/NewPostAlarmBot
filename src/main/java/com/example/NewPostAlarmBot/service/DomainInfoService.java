package com.example.NewPostAlarmBot.service;


import com.example.NewPostAlarmBot.DTO.DomainInfoDto;
import com.example.NewPostAlarmBot.domain.DomainInfo;
import com.example.NewPostAlarmBot.repository.DomainInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class DomainInfoService {
    private final DomainInfoRepo domainInfoRepo;

    public void save(DomainInfoDto dto){
        Optional<DomainInfo> b = domainInfoRepo.findByUrl(dto.url);
        if(b.isPresent())
            update(dto.url, dto);
        else
            domainInfoRepo.save(dto.toEntity());
    }

    public void update(String url, DomainInfoDto dto){
        DomainInfo domainInfo = domainInfoRepo.findByUrl(url).orElseThrow(()->new IllegalArgumentException("Update: Not found URL"));
        domainInfo.setUrlTitle(dto.urlTitle);
        domainInfo.setLoginId(dto.loginId);
        domainInfo.setLoginPw(dto.loginPw);
    }

    public List<DomainInfoDto> findAll(){
        List<DomainInfoDto> tmp = new ArrayList<>();

        for(DomainInfo d: domainInfoRepo.findAll())
            tmp.add(new DomainInfoDto(d));
        return tmp;
    }

    public DomainInfoDto findById(Integer id){
        DomainInfo domainInfo = domainInfoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Find By Id: Not found ID"));
        return new DomainInfoDto((domainInfo));
    }

    public DomainInfoDto findByUrl(String url) {
        DomainInfo domainInfo = domainInfoRepo.findByUrl(url).orElseThrow(() -> new IllegalArgumentException("Find By Url: Not found URL"));
        return new DomainInfoDto(domainInfo);
    }

    public List<DomainInfoDto> findByChatId(Long chatId){
        List<DomainInfoDto> tmp = new ArrayList<>();

        for(DomainInfo d: domainInfoRepo.findByChatId(chatId))
            tmp.add(new DomainInfoDto(d));
        return tmp;
    }

    public void delete(DomainInfoDto dto){
        DomainInfo domainInfo = domainInfoRepo.findByUrl(dto.getUrl()).orElseThrow(() -> new IllegalArgumentException("Delete: Not found URL"));
        domainInfoRepo.delete(domainInfo);
    }
}
