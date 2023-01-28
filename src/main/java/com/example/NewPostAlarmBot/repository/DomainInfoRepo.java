package com.example.NewPostAlarmBot.repository;

import com.example.NewPostAlarmBot.domain.DomainInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainInfoRepo extends JpaRepository<DomainInfo, Integer> {

    Optional<DomainInfo> findByUrl(String url);

    List<DomainInfo> findAll();

    DomainInfo save( DomainInfo domainId);

    List<DomainInfo> findByChatId(Long chatId);
    Optional<DomainInfo> findById(Integer id);
}
